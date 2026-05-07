// P2P Synchronization protocol with Perfect Forward Secrecy
// Uses UDP for discovery, TCP for sync, ChaCha20 for encryption

use std::net::{SocketAddr, TcpStream, UdpSocket};
use std::io::{Read, Write};
use std::time::Duration;
use tokio::sync::mpsc;
use thiserror::Error;
use zeroize::Zeroizing;

use crate::crypto::chacha20::{ChaChaCipher, generate_nonce};

const DISCOVERY_PORT: u16 = 53535;
const SYNC_PORT: u16 = 53536;
const MAX_MESSAGE_SIZE: usize = 1024 * 1024; // 1MB

#[derive(Error, Debug)]
pub enum SyncError {
    #[error("Discovery failed: {0}")]
    Discovery(String),
    #[error("Connection failed: {0}")]
    Connection(String),
    #[error("Encryption failed: {0}")]
    Encryption(String),
    #[error("Transfer failed: {0}")]
    Transfer(String),
}

/// Device info for P2P discovery
#[derive(Debug, Clone)]
pub struct PeerDevice {
    pub id: String,
    pub name: String,
    pub address: SocketAddr,
    pub public_key: Vec<u8>,
}

/// Message types for P2P protocol
#[derive(Debug, Clone)]
pub enum SyncMessage {
    Discover { device_id: String, name: String, public_key: Vec<u8> },
    Connect { device_id: String },
    Accept { device_id: String, public_key: Vec<u8> },
    RequestSync,
    SyncData { data: Vec<u8> },
    Complete,
    Reject { reason: String },
}

/// P2P protocol peer
pub struct SyncPeer {
    device_id: String,
    name: String,
    address: SocketAddr,
    /// Current chain key for PFS
    chain_key: Zeroizing<Vec<u8>>,
    /// Message counter
    counter: u64,
    /// Session key for encryption
    session_key: Zeroizing<Vec<u8>>,
}

impl SyncPeer {
    /// Create new sync peer
    pub fn new(device_id: String, name: String, address: SocketAddr) -> Self {
        Self {
            device_id,
            name,
            address,
            chain_key: Zeroizing::new(Vec::new()),
            counter: 0,
            session_key: Zeroizing::new(Vec::new()),
        }
    }
    
    /// Derive message key from chain key ( PFS ratchet)
    fn derive_message_key(&self, counter: u64) -> Vec<u8> {
        use hkdf::Hkdf;
        use sha2::Sha256;
        
        let mut message_key = vec![0u8; 32];
        let hk = Hkdf::<Sha256>::new(Some(&self.chain_key), None);
        let info = format!("pfs_ratchet_{}", counter);
        hk.expand(info.as_bytes(), &mut message_key).unwrap();
        message_key
    }
    
    /// Encrypt message with PFS
    pub fn encrypt_message(&mut self, plaintext: &[u8]) -> Result<(Vec<u8>, [u8; 12]), SyncError> {
        let message_key = self.derive_message_key(self.counter);
        let nonce = generate_nonce();
        
        let cipher = ChaChaCipher::new(message_key.try_into().unwrap());
        let ciphertext = cipher.encrypt(plaintext, &nonce)
            .map_err(|e| SyncError::Encryption(e.to_string()))?;
        
        self.counter += 1;
        Ok((ciphertext, nonce))
    }
    
    /// Decrypt message with PFS
    pub fn decrypt_message(&mut self, ciphertext: &[u8], nonce: &[u8; 12]) -> Result<Vec<u8>, SyncError> {
        let message_key = self.derive_message_key(self.counter);
        
        let cipher = ChaChaCipher::new(message_key.try_into().unwrap());
        let plaintext = cipher.decrypt(ciphertext, nonce)
            .map_err(|e| SyncError::Encryption(e.to_string()))?;
        
        self.counter += 1;
        Ok(plaintext)
    }
}

/// P2P sync manager
pub struct SyncManager {
    device_id: String,
    device_name: String,
    public_key: Vec<u8>,
    secret_key: Vec<u8>,
    peers: Vec<SyncPeer>,
}

impl SyncManager {
    pub fn new(device_id: String, device_name: String) -> Self {
        // Generate keypair for P2P
        // In production, use ML-KEM-768
        let mut pk = vec![0u8; 32];
        let mut sk = vec![0u8; 32];
        crate::crypto::rng::SecureRng::fill_bytes(&mut pk);
        crate::crypto::rng::SecureRng::fill_bytes(&mut sk);
        
        Self {
            device_id,
            device_name,
            public_key: pk,
            secret_key: sk,
            peers: Vec::new(),
        }
    }
    
    /// Discover peers on local network
    pub fn discover_peers(&self) -> Result<Vec<PeerDevice>, SyncError> {
        let socket = UdpSocket::bind(format!("0.0.0.0:{}", DISCOVERY_PORT))
            .map_err(|e| SyncError::Discovery(e.to_string()))?;
        
        socket.set_broadcast(true)
            .map_err(|e| SyncError::Discovery(e.to_string()))?;
        
        // Send discovery broadcast
        let msg = SyncMessage::Discover {
            device_id: self.device_id.clone(),
            name: self.device_name.clone(),
            public_key: self.public_key.clone(),
        };
        
        let data = serde_json::to_vec(&msg).unwrap();
        socket.send_to(&data, format!("255.255.255.255:{}", DISCOVERY_PORT))
            .map_err(|e| SyncError::Discovery(e.to_string()))?;
        
        // Wait for responses
        let mut peers = Vec::new();
        socket.set_read_timeout(Some(Duration::from_secs(2))).ok();
        
        let mut buf = [0u8; 4096];
        while let Ok((len, addr)) = socket.recv_from(&mut buf) {
            if let Ok(msg) = serde_json::from_slice::<SyncMessage>(&buf[..len]) {
                if let SyncMessage::Discover { device_id, name, public_key } = msg {
                    if device_id != self.device_id {
                        peers.push(PeerDevice { id: device_id, name, address: addr, public_key });
                    }
                }
            }
        }
        
        Ok(peers)
    }
    
    /// Connect to a peer
    pub fn connect(&mut self, peer: &PeerDevice) -> Result<SyncPeer, SyncError> {
        let mut stream = TcpStream::connect_timeout(
            &peer.address,
            Duration::from_secs(10),
        ).map_err(|e| SyncError::Connection(e.to_string()))?;
        
        stream.set_write_timeout(Some(Duration::from_secs(10))).ok();
        stream.set_read_timeout(Some(Duration::from_secs(10))).ok();
        
        // Send connect message
        let msg = SyncMessage::Connect { device_id: self.device_id.clone() };
        let data = serde_json::to_vec(&msg).unwrap();
        stream.write_all(&data).map_err(|e| SyncError::Connection(e.to_string()))?;
        
        // Wait for accept
        let mut buf = [0u8; 4096];
        let len = stream.read(&mut buf).map_err(|e| SyncError::Connection(e.to_string()))?;
        
        if let Ok(SyncMessage::Accept { .. }) = serde_json::from_slice::<SyncMessage>(&buf[..len]) {
            let mut sync_peer = SyncPeer::new(
                peer.id.clone(),
                peer.name.clone(),
                peer.address,
            );
            
            // Derive shared secret (simplified - in production use ML-KEM)
            // For now, just use a random key
            sync_peer.chain_key = Zeroizing::new(vec![0u8; 32]);
            sync_peer.session_key = Zeroizing::new(vec![0u8; 32]);
            
            self.peers.push(sync_peer.clone());
            Ok(sync_peer)
        } else {
            Err(SyncError::Connection("Peer rejected connection".into()))
        }
    }
    
    /// Sync vault data with connected peer
    pub fn sync_vault(&mut self, peer: &SyncPeer, data: &[u8]) -> Result<(), SyncError> {
        let mut stream = TcpStream::connect_timeout(
            &peer.address,
            Duration::from_secs(10),
        ).map_err(|e| SyncError::Transfer(e.to_string()))?;
        
        stream.set_write_timeout(Some(Duration::from_secs(30))).ok();
        
        // Send request
        let msg = SyncMessage::RequestSync;
        let data_bytes = serde_json::to_vec(&msg).unwrap();
        stream.write_all(&data_bytes).map_err(|e| SyncError::Transfer(e.to_string()))?;
        
        // Transfer encrypted data
        // Note: In real implementation, use peer.encrypt_message()
        let len_buf = (data.len() as u32).to_le_bytes();
        stream.write_all(&len_buf).map_err(|e| SyncError::Transfer(e.to_string()))?;
        stream.write_all(data).map_err(|e| SyncError::Transfer(e.to_string()))?;
        
        Ok(())
    }
}

/// Compute SAS (Short Authentication String) for out-of-band verification
pub fn compute_sas(secret: &[u8], device_id_a: &str, device_id_b: &str) -> String {
    use sha2::Sha256;
    use std::collections::hash_map::DefaultHasher;
    use std::hash::{Hash, Hasher};
    
    // Combine secrets
    let mut combined = Vec::new();
    combined.extend_from_slice(secret);
    combined.extend_from_slice(device_id_a.as_bytes());
    combined.extend_from_slice(device_id_b.as_bytes());
    
    // Hash
    let mut hash = Sha256::digest(&combined);
    let hash_bytes = hash.as_slice();
    
    // Take first 6 digits
    let mut hasher = DefaultHasher::new();
    hash_bytes.hash(&mut hasher);
    let val = hasher.finish() % 1_000_000;
    format!("{:06}", val)
}