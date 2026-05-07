// PQ Vault - SecureVault Core Library
// Post-quantum password manager with ML-KEM and ML-DSA support

pub mod crypto;
pub mod vault;
pub mod totp;
pub mod sync;
pub mod passkey;
pub mod generator;

pub use error::Error;
pub use vault::Vault;
pub use generator::PasswordGenerator;

use zeroize::Zeroizing;

/// Main library entry point
pub fn init() -> Result<(), Error> {
    tracing_subscriber::fmt()
        .with_max_level(tracing::Level::WARN)
        .init();
    Ok(())
}

mod error {
    use thiserror::Error;

    #[derive(Error, Debug)]
    pub enum Error {
        #[error("Crypto error: {0}")]
        Crypto(String),
        
        #[error("Vault error: {0}")]
        Vault(String),
        
        #[error("Entry not found: {0}")]
        EntryNotFound(String),
        
        #[error("Invalid key: {0}")]
        InvalidKey(String),
        
        #[error("Encryption failed: {0}")]
        EncryptionFailed(String),
        
        #[error("Decryption failed: {0}")]
        DecryptionFailed(String),
        
        #[error("IO error: {0}")]
        Io(#[from] std::io::Error),
        
        #[error("Serialization error: {0}")]
        Serde(#[from] serde_json::Error),
        
        #[error("TOTP error: {0}")]
        Totp(String),
        
        #[error("Sync error: {0}")]
        Sync(String),
        
        #[error("Passkey error: {0}")]
        Passkey(String),
    }
}

mod generator {
    use rand::{Rng,_rngs::StdRng};
    use rand::SeedableRng;
    
    /// Character sets for password generation
    const LOWERCASE: &[u8] = b"abcdefghijklmnopqrstuvwxyz";
    const UPPERCASE: &[u8] = b"ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const DIGITS: &[u8] = b"0123456789";
    const SYMBOLS: &[u8] = b"!@#$%^&*()_+-=[]{}|;:,.<>?";
    const AMBIGUOUS: &[u8] = b"0O1lI";
    
    #[derive(Clone, Debug)]
    pub struct PasswordOptions {
        pub length: usize,
        pub uppercase: bool,
        pub lowercase: bool,
        pub numbers: bool,
        pub symbols: bool,
        pub exclude_ambiguous: bool,
    }
    
    impl Default for PasswordOptions {
        fn default() -> Self {
            Self {
                length: 16,
                uppercase: true,
                lowercase: true,
                numbers: true,
                symbols: true,
                exclude_ambiguous: false,
            }
        }
    }
    
    pub struct PasswordGenerator {
        rng: StdRng,
    }
    
    impl PasswordGenerator {
        pub fn new() -> Self {
            Self {
                rng: StdRng::from_entropy(),
            }
        }
        
        pub fn with_seed(seed: [u8; 32]) -> Self {
            Self {
                rng: StdRng::from_seed(seed),
            }
        }
        
        pub fn generate(&mut self, options: &PasswordOptions) -> String {
            let mut charset = Vec::new();
            
            if options.lowercase {
                charset.extend_from_slice(LOWERCASE);
            }
            if options.uppercase {
                charset.extend_from_slice(UPPERCASE);
            }
            if options.numbers {
                charset.extend_from_slice(DIGITS);
            }
            if options.symbols {
                charset.extend_from_slice(SYMBOLS);
            }
            
            if charset.is_empty() {
                charset.extend_from_slice(LOWERCASE);
            }
            
            // Remove ambiguous characters if requested
            if options.exclude_ambiguous {
                charset.retain(|b| !AMBIGUOUS.contains(b));
            }
            
            let charset = charset;
            let len = charset.len();
            
            (0..options.length)
                .map(|_| charset[self.rng.gen_range(0..len)] as char)
                .collect()
        }
        
        /// Generate passphrase with words
        pub fn generate_passphrase(&mut self, word_count: usize, separator: &str, capitalize: bool) -> String {
            // Simple word list - in production, use EFF large wordlist
            let words = [
                "apple", "banana", "cherry", "delta", "echo", "foxtrot", "golf", "hotel",
                "india", "juliet", "kilo", "lima", "mike", "november", "oscar", "papa",
                "quebec", "romeo", "sierra", "tango", "uniform", "victor", "whiskey", "xray",
                "yankee", "zulu", "anchor", "bravo", "crater", "danger", "eagle", "falcon",
                "garden", "harbor", "island", "jungle", "kingdom", "lemon", "maple", "nation",
                "ocean", "planet", "quest", "river", "storm", "token", "unity", "valley",
                "winter", "xenon", "youth", "zone"
            ];
            
            let count = word_count.min(words.len());
            let selected: Vec<&str> = (0..count)
                .map(|_| words[self.rng.gen_range(0..words.len())])
                .collect();
            
            let result: String = if capitalize {
                selected
                    .iter()
                    .map(|w| {
                        let mut c = w.chars();
                        match c.next() {
                            None => String::new(),
                            Some(f) => f.to_uppercase().chain(c).collect(),
                        }
                    })
                    .collect::<Vec<_>>()
                    .join(separator)
            } else {
                selected.join(separator)
            };
            
            // Add a random number at the end
            let number = self.rng.gen_range(0..100);
            format!("{}{}{}", result, separator, number)
        }
    }
    
    impl Default for PasswordGenerator {
        fn default() -> Self {
            Self::new()
        }
    }
    
    #[cfg(test)]
    mod tests {
        use super::*;
        
        #[test]
        fn test_generate_password() {
            let mut gen = PasswordGenerator::new();
            let options = PasswordOptions::default();
            let password = gen.generate(&options);
            
            assert_eq!(password.len(), 16);
            println!("Generated password: {}", password);
        }
        
        #[test]
        fn test_generate_passphrase() {
            let mut gen = PasswordGenerator::new();
            let passphrase = gen.generate_passphrase(4, "-", true);
            
            assert_eq!(passphrase.split('-').count(), 5); // 4 words + 1 number
            println!("Generated passphrase: {}", passphrase);
        }
    }
}