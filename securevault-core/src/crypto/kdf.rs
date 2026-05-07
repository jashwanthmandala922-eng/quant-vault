// Key derivation functions - HKDF and Argon2id
// Used for deriving keys from OAuth tokens and passwords

use hkdf::Hkdf;
use sha2::Sha256;
use thiserror::Error;
use zeroize::Zeroizing;

#[derive(Error, Debug)]
pub enum KDFError {
    #[error("HKDF error: {0}")]
    HKDF(String),
    
    #[error("Argon2 error: {0}")]
    Argon2(String),
}

/// HKDF-SHA256 key derivation
pub fn hkdf_expand(
    ikm: &[u8],
    info: &[u8],
    okm: &mut [u8],
) {
    let hk = Hkdf::<Sha256>::new(Some(ikm), None);
    hk.expand(info, okm).expect("OKM length is valid");
}

/// Derive master key from OAuth token using HKDF
pub fn derive_master_key(oauth_token: &[u8], salt: &[u8]) -> Zeroizing<Vec<u8>> {
    let mut master_key = Zeroizing::new(vec![0u8; 32]);
    hkdf_expand(oauth_token, b"pq-vault-master", &mut master_key);
    master_key
}

/// Derive session key from master key
pub fn derive_session_key(
    master_key: &[u8],
    salt: &[u8],
) -> Zeroizing<Vec<u8>> {
    let mut session_key = Zeroizing::new(vec![0u8; 32]);
    hkdf_expand(master_key, salt, &mut session_key);
    session_key
}

/// Dual-key derivation: combines OAuth key with local PIN/biometric key
pub fn derive_dual_key(
    oauth_key: &[u8],
    local_key: &[u8],
) -> Zeroizing<Vec<u8>> {
    // XOR the keys together
    let mut combined = Vec::with_capacity(oauth_key.len().max(local_key.len()));
    for (a, b) in oauth_key.iter().zip(local_key.iter()) {
        combined.push(a ^ b);
    }
    // If one is longer, append the rest
    if oauth_key.len() > local_key.len() {
        for a in oauth_key.iter().skip(local_key.len()) {
            combined.push(*a);
        }
    } else if local_key.len() > oauth_key.len() {
        for b in local_key.iter().skip(oauth_key.len()) {
            combined.push(*b);
        }
    }
    
    // Expand to 32 bytes
    let mut dual_key = Zeroizing::new(vec![0u8; 32]);
    hkdf_expand(&combined, b"pq-vault-dual-key", &mut dual_key);
    dual_key
}

/// Generate random salt for key derivation
pub fn generate_salt() -> [u8; 32] {
    let mut salt = [0u8; 32];
    rand::thread_rng().fill_bytes(&mut salt);
    salt
}

// Argon2id password hashing (used for PIN hardening)
pub mod argon2 {
    use argon2::{
        password_hash::{rand_core::OsRng, PasswordHash, PasswordHasher, PasswordVerifier, SaltString},
        Argon2,
    };
    use thiserror::Error;
    
    #[derive(Error, Debug)]
    pub enum Argon2Error {
        #[error("Hash failed: {0}")]
        Hash(String),
        
        #[error("Verify failed: {0}")]
        Verify(String),
    }
    
    /// Hash a PIN with Argon2id (256MB, 4 iterations)
    pub fn hash_pin(pin: &str) -> Result<String, Argon2Error> {
        let salt = SaltString::generate(&mut OsRng);
        let argon2 = Argon2::new(
            argon2::Algorithm::Argon2id,
            argon2::Version::V0x13,
            argon2::Params::new(262144, 4, 4, Some(32)).unwrap(),
        );
        
        let hash = argon2
            .hash_password(pin.as_bytes(), &salt)
            .map_err(|e| Argon2Error::Hash(e.to_string()))?
            .to_string();
        
        Ok(hash)
    }
    
    /// Verify a PIN against its hash
    pub fn verify_pin(pin: &str, hash: &str) -> Result<bool, Argon2Error> {
        let parsed_hash = PasswordHash::new(hash)
            .map_err(|e| Argon2Error::Verify(e.to_string()))?;
        
        let argon2 = Argon2::new(
            argon2::Algorithm::Argon2id,
            argon2::Version::V0x13,
            argon2::Params::new(262144, 4, 4, Some(32)).unwrap(),
        );
        
        let result = argon2
            .verify_password(pin.as_bytes(), &parsed_hash)
            .is_ok();
        
        Ok(result)
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    
    #[test]
    fn test_hkdf_derive() {
        let token = b"oauth_token_12345";
        let master_key = derive_master_key(token, b"salt");
        
        assert_eq!(master_key.len(), 32);
    }
    
    #[test]
    fn test_dual_key() {
        let oauth = b"oauth_key_12345678901234567890";
        let local = b"local_key_12345678901234567890";
        
        let dual = derive_dual_key(oauth, local);
        
        assert_eq!(dual.len(), 32);
    }
    
    #[test]
    fn test_argon2_pin() {
        let pin = "12345678";
        let hash = argon2::hash_pin(pin).unwrap();
        let valid = argon2::verify_pin(pin, &hash).unwrap();
        
        assert!(valid);
    }
}