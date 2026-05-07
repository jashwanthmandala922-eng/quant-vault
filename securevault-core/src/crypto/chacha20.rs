// ChaCha20-Poly1305 for P2P sync encryption
use chacha20poly1305::{
    aead::{Aead, KeyInit, OsRng},
    ChaCha20Poly1305, Nonce,
};
use zeroize::{Zeroize, Zeroizing};
use thiserror::Error;

const NONCE_SIZE: usize = 12;

#[derive(Error, Debug)]
pub enum ChaChaError {
    #[error("Encryption failed: {0}")]
    Encrypt(String),
    #[error("Decryption failed: {0}")]
    Decrypt(String),
}

/// Generate random 96-bit nonce for ChaCha20
pub fn generate_nonce() -> [u8; NONCE_SIZE] {
    let mut nonce = [0u8; NONCE_SIZE];
    OsRng.fill_bytes(&mut nonce);
    nonce
}

/// Encrypt with ChaCha20-Poly1305
pub fn encrypt(plaintext: &[u8], key: &[u8], nonce: &[u8; NONCE_SIZE]) -> Result<Vec<u8>, ChaChaError> {
    let cipher = ChaCha20Poly1305::new_from_slice(key)
        .map_err(|e| ChaChaError::Encrypt(e.to_string()))?;
    let n = Nonce::from_slice(nonce);
    cipher.encrypt(n, plaintext)
        .map_err(|e| ChaChaError::Encrypt(e.to_string()))
}

/// Decrypt with ChaCha20-Poly1305
pub fn decrypt(ciphertext: &[u8], key: &[u8], nonce: &[u8; NONCE_SIZE]) -> Result<Vec<u8>, ChaChaError> {
    let cipher = ChaCha20Poly1305::new_from_slice(key)
        .map_err(|e| ChaChaError::Decrypt(e.to_string()))?;
    let n = Nonce::from_slice(nonce);
    cipher.decrypt(n, ciphertext)
        .map_err(|e| ChaChaError::Decrypt(e.to_string()))
}

/// Stateful ChaCha20 cipher for P2P
pub struct ChaChaCipher {
    key: [u8; 32],
}

impl ChaChaCipher {
    pub fn new(key: [u8; 32]) -> Self {
        Self { key }
    }
    
    pub fn encrypt(&self, plaintext: &[u8], nonce: &[u8; NONCE_SIZE]) -> Result<Vec<u8>, ChaChaError> {
        encrypt(plaintext, &self.key, nonce)
    }
    
    pub fn decrypt(&self, ciphertext: &[u8], nonce: &[u8; NONCE_SIZE]) -> Result<Vec<u8>, ChaChaError> {
        decrypt(ciphertext, &self.key, nonce)
    }
}

impl Drop for ChaChaCipher {
    fn drop(&mut self) {
        self.key.zeroize();
    }
}