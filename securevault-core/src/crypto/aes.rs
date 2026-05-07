// AES-256-GCM encryption for symmetric encryption
// Used for vault encryption

use aes_gcm::{
    aead::{Aead, KeyInit, OsRng},
    Aes256Gcm, Nonce,
};
use zeroize::{Zeroize, Zeroizing};
use thiserror::Error;

const NONCE_SIZE: usize = 12;
const KEY_SIZE: usize = 32;

#[derive(Error, Debug)]
pub enum AeadError {
    #[error("Encryption failed: {0}")]
    Encrypt(String),
    
    #[error("Decryption failed: {0}")]
    Decrypt(String),
    
    #[error("Invalid key size: expected {0}, got {1}")]
    InvalidKeySize(usize, usize),
    
    #[error("Invalid nonce size: expected {0}, got {1}")]
    InvalidNonceSize(usize, usize),
}

/// Generate a random 256-bit key
pub fn generate_key() -> Zeroizing<Vec<u8>> {
    let mut key = Zeroizing::new(vec![0u8; KEY_SIZE]);
    OsRng.fill_bytes(&mut key);
    key
}

/// Generate a random 96-bit nonce
pub fn generate_nonce() -> [u8; NONCE_SIZE] {
    let mut nonce = [0u8; NONCE_SIZE];
    OsRng.fill_bytes(&mut nonce);
    nonce
}

/// Encrypt data with AES-256-GCM
pub fn encrypt(plaintext: &[u8], key: &[u8], nonce: &[u8; NONCE_SIZE]) -> Result<Vec<u8>, AeadError> {
    if key.len() != KEY_SIZE {
        return Err(AeadError::InvalidKeySize(KEY_SIZE, key.len()));
    }
    if nonce.len() != NONCE_SIZE {
        return Err(AeadError::InvalidNonceSize(NONCE_SIZE, nonce.len()));
    }
    
    let cipher = Aes256Gcm::new_from_slice(key)
        .map_err(|e| AeadError::Encrypt(e.to_string()))?;
    
    let n = Nonce::from_slice(nonce);
    let ciphertext = cipher
        .encrypt(n, plaintext)
        .map_err(|e| AeadError::Encrypt(e.to_string()))?;
    
    Ok(ciphertext)
}

/// Decrypt data with AES-256-GCM
pub fn decrypt(ciphertext: &[u8], key: &[u8], nonce: &[u8; NONCE_SIZE]) -> Result<Vec<u8>, AeadError> {
    if key.len() != KEY_SIZE {
        return Err(AeadError::InvalidKeySize(KEY_SIZE, key.len()));
    }
    if nonce.len() != NONCE_SIZE {
        return Err(AeadError::InvalidNonceSize(NONCE_SIZE, nonce.len()));
    }
    
    let cipher = Aes256Gcm::new_from_slice(key)
        .map_err(|e| AeadError::Decrypt(e.to_string()))?;
    
    let n = Nonce::from_slice(nonce);
    let plaintext = cipher
        .decrypt(n, ciphertext)
        .map_err(|e| AeadError::Decrypt(e.to_string()))?;
    
    Ok(plaintext)
}

/// AESCipher - stateful AES-GCM cipher
pub struct AESCipher {
    key: Zeroizing<Vec<u8>>,
    cipher: Aes256Gcm,
}

impl AESCipher {
    pub fn new(key: impl Into<Zeroizing<Vec<u8>>) -> Self {
        let key = key.into();
        let cipher = Aes256Gcm::new_from_slice(&key)
            .expect("Valid AES-256 key");
        Self { key, cipher }
    }
    
    pub fn encrypt(&self, plaintext: &[u8]) -> (Vec<u8>, [u8; NONCE_SIZE]) {
        let nonce = generate_nonce();
        let n = Nonce::from_slice(&nonce);
        let ciphertext = self.cipher
            .encrypt(n, plaintext)
            .expect("Encryption should succeed");
        (ciphertext, nonce)
    }
    
    pub fn decrypt(&self, ciphertext: &[u8], nonce: &[u8; NONCE_SIZE]) -> Result<Vec<u8>, AeadError> {
        let n = Nonce::from_slice(nonce);
        self.cipher
            .decrypt(n, ciphertext)
            .map_err(|e| AeadError::Decrypt(e.to_string()))
    }
}

impl Drop for AESCipher {
    fn drop(&mut self) {
        self.key.zeroize();
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    
    #[test]
    fn test_encrypt_decrypt() {
        let key = generate_key();
        let nonce = generate_nonce();
        
        let plaintext = b"Hello, PQ Vault!";
        let ciphertext = encrypt(plaintext, &key, &nonce).unwrap();
        let decrypted = decrypt(&ciphertext, &key, &nonce).unwrap();
        
        assert_eq!(plaintext.as_slice(), decrypted.as_slice());
    }
    
    #[test]
    fn test_aes_cipher() {
        let key = generate_key();
        let cipher = AESCipher::new(key.clone());
        
        let plaintext = b"Test message";
        let (ciphertext, nonce) = cipher.encrypt(plaintext);
        let decrypted = cipher.decrypt(&ciphertext, &nonce).unwrap();
        
        assert_eq!(plaintext, decrypted.as_slice());
    }
}