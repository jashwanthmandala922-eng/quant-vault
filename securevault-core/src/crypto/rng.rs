// Cryptographically secure random number generator
use rand::{RngCore, CryptoRng};
use rand::rngs::OsRng;

/// CSPRNG wrapper
pub struct SecureRng;

impl SecureRng {
    /// Fill buffer with random bytes
    pub fn fill_bytes(data: &mut [u8]) {
        OsRng.fill_bytes(data);
    }
    
    /// Generate random bytes of given length
    pub fn random_bytes(len: usize) -> Vec<u8> {
        let mut bytes = vec![0u8; len];
        OsRng.fill_bytes(&mut bytes);
        bytes
    }
    
    /// Generate random u32
    pub fn random_u32() -> u32 {
        let mut bytes = [0u8; 4];
        OsRng.fill_bytes(&mut bytes);
        u32::from_le_bytes(bytes)
    }
    
    /// Generate random u64
    pub fn random_u64() -> u64 {
        let mut bytes = [0u8; 8];
        OsRng.fill_bytes(&mut bytes);
        u64::from_le_bytes(bytes)
    }
}