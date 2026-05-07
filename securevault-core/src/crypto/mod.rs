// Crypto module exports

pub mod aes;
pub mod chacha20;
pub mod kdf;
pub mod rng;

pub use aes::{AESCipher, generate_key, generate_nonce, encrypt, decrypt};
pub use chacha20::{ChaChaCipher, ChaChaError};
pub use kdf::{hkdf_expand, derive_master_key, derive_session_key, derive_dual_key, argon2};
pub use rng::SecureRng;

use thiserror::Error;

#[derive(Error, Debug)]
pub enum CryptoError {
    #[error("Encryption failed: {0}")]
    EncryptionFailed(String),
    
    #[error("Decryption failed: {0}")]
    DecryptionFailed(String),
    
    #[error("Key derivation failed: {0}")]
    KeyDerivationFailed(String),
}

// Re-export PQ modules when feature is enabled
#[cfg(feature = "pq")]
pub mod pq {
    pub mod kem;
    pub mod sig;
    pub use kem::{pq_kem, pq_sig};
}