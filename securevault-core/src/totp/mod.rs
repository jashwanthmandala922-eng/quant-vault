// TOTP (Time-based One-Time Password) generator
// Implements RFC 6238

use totp_lite::{TOTP, SHA1};
use base32::Alphabet;
use thiserror::Error;
use zeroize::Zeroizing;

#[derive(Error, Debug)]
pub enum TotpError {
    #[error("Invalid secret: {0}")]
    InvalidSecret(String),
    
    #[error("Generation failed: {0}")]
    GenerationFailed(String),
}

/// Generate a 6-digit TOTP code
pub fn generate_code(secret: &[u8], time_step: u64) -> Result<String, TotpError> {
    let totp = TOTP::new(
        secrets,
        6,
        1,
        30,
        SHA1,
    ).map_err(|e| TotpError::InvalidSecret(e.to_string()))?;
    
    Ok(totp.generate(time_step))
}

/// Generate TOTP for current time
pub fn generate_current(secret: &[u8]) -> Result<String, TotpError> {
    let now = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .map_err(|e| TotpError::GenerationFailed(e.to_string()))?
        .as_secs();
    
    generate_code(secret, now / 30)
}

/// Parse Base32-encoded secret
pub fn parse_secret(encoded: &str) -> Result<Vec<u8>, TotpError> {
    // Remove spaces and convert to uppercase
    let cleaned: String = encoded
        .chars()
        .filter(|c| !c.is_whitespace())
        .collect();
    
    base32::decode(Alphabet::Rfc4648 { padded: false }, &cleaned)
        .ok_or_else(|| TotpError::InvalidSecret("Invalid Base32".into()))
}

/// TOTP generator struct for convenience
pub struct TotpGenerator {
    totp: TOTP<SHA1>,
}

impl TotpGenerator {
    pub fn new(secret: &[u8]) -> Result<Self, TotpError> {
        let totp = TOTP::new(
            secret,
            6,
            1,
            30,
            SHA1,
        ).map_err(|e| TotpError::InvalidSecret(e.to_string()))?;
        
        Ok(Self { totp })
    }
    
    pub fn generate(&self) -> String {
        let now = std::time::SystemTime::now()
            .duration_since(std::time::UNIX_EPOCH)
            .unwrap()
            .as_secs();
        
        self.totp.generate(now / 30)
    }
    
    pub fn remaining_seconds(&self) -> u64 {
        let now = std::time::SystemTime::now()
            .duration_since(std::time::UNIX_EPOCH)
            .unwrap()
            .as_secs();
        
        30 - (now % 30)
    }
    
    pub fn generate_at(&self, time: u64) -> String {
        self.totp.generate(time)
    }
}

/// Vault TOTP entry
#[derive(Debug, Clone)]
pub struct TotpEntry {
    pub id: String,
    pub issuer: String,
    pub account: String,
    pub secret: Zeroizing<Vec<u8>>,
}

impl TotpEntry {
    pub fn new(id: String, issuer: String, account: String, secret: Vec<u8>) -> Self {
        Self {
            id,
            issuer,
            account,
            secret: Zeroizing::new(secret),
        }
    }
    
    pub fn generate(&self) -> Result<String, TotpError> {
        generate_current(&self.secret)
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    
    #[test]
    fn test_generate_totp() {
        // Test with known secret
        let secret = b"JBSWY3DPEHPK3PXP";
        let code = generate_current(secret).unwrap();
        
        println!("Generated code: {}", code);
        assert_eq!(code.len(), 6);
    }
    
    #[test]
    fn test_totp_generator() {
        let secret = b"JBSWY3DPEHPK3PXP";
        let generator = TotpGenerator::new(secret).unwrap();
        
        let code = generator.generate();
        let remaining = generator.remaining_seconds();
        
        println!("Code: {}, remaining: {}s", code, remaining);
        assert_eq!(code.len(), 6);
        assert!(remaining <= 30);
    }
}