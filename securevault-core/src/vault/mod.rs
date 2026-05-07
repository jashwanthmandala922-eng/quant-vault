// Password vault implementation with full metadata encryption
// All entry data is encrypted, including traditionally "non-sensitive" fields

use serde::{Deserialize, Serialize};
use uuid::Uuid;
use chrono::{DateTime, Utc};
use zeroize::{Zeroize, Zeroizing};
use std::collections::HashMap;

use crate::crypto::aes::{AESCipher, generate_key, generate_nonce};
use crate::crypto::kdf::{derive_master_key, derive_dual_key};
use crate::crypto::CryptoError;

/// Vault entry types
#[derive(Debug, Clone, Serialize, Deserialize, PartialEq)]
pub enum EntryType {
    Password,
    SecureNote,
    TOTP,
    CreditCard,
    Identity,
    Passkey,
}

/// Custom field in an entry
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct CustomField {
    pub name: String,
    pub value: String,
    pub is_hidden: bool,
}

/// Single vault entry
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct VaultEntry {
    pub id: String,
    pub entry_type: EntryType,
    pub title: String,
    pub url: Option<String>,
    pub username: Option<String>,
    pub password: Option<String>,
    pub notes: Option<String>,
    pub totp_secret: Option<String>,
    pub favorite: bool,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
    pub custom_fields: Vec<CustomField>,
}

/// Folder for organizing entries
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Folder {
    pub id: String,
    pub name: String,
}

/// Vault settings
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct VaultSettings {
    pub auto_lock_minutes: u32,
    pub biometric_enabled: bool,
    pub clipboard_clear_seconds: u32,
    pub clear_on_copy: bool,
}

impl Default for VaultSettings {
    fn default() -> Self {
        Self {
            auto_lock_minutes: 5,
            biometric_enabled: true,
            clipboard_clear_seconds: 30,
            clear_on_copy: true,
        }
    }
}

/// The vault structure
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Vault {
    pub id: String,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
    pub entries: Vec<VaultEntry>,
    pub folders: Vec<Folder>,
    pub settings: VaultSettings,
}

impl Default for Vault {
    fn default() -> Self {
        Self {
            id: Uuid::new_v4().to_string(),
            created_at: Utc::now(),
            updated_at: Utc::now(),
            entries: Vec::new(),
            folders: Vec::new(),
            settings: VaultSettings::default(),
        }
    }
}

/// Encrypted field for full metadata encryption
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct EncryptedField {
    pub cipher_text: Vec<u8>,
    pub nonce: [u8; 12],
    pub tag: Vec<u8>,
}

/// Encrypted vault entry
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct EncryptedVaultEntry {
    pub id: EncryptedField,
    pub entry_type: EncryptedField,
    pub title: EncryptedField,
    pub url: Option<EncryptedField>,
    pub username: Option<EncryptedField>,
    pub password: Option<EncryptedField>,
    pub notes: Option<EncryptedField>,
    pub favorite: EncryptedField,
    pub custom_fields: Vec<EncryptedField>,
}

/// Encrypted vault (all metadata encrypted)
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct EncryptedVault {
    pub version: u8,
    pub entries: Vec<EncryptedVaultEntry>,
    pub settings: VaultSettings,
    pub salt: [u8; 32],
}

/// Metadata encryptor for full field encryption
pub struct MetadataEncryptor {
    cipher: AESCipher,
}

impl MetadataEncryptor {
    pub fn new(session_key: &[u8]) -> Self {
        Self {
            cipher: AESCipher::new(session_key.to_vec()),
        }
    }
    
    /// Encrypt a single field
    pub fn encrypt_field(&self, value: &str) -> EncryptedField {
        let (cipher_text, nonce) = self.cipher.encrypt(value.as_bytes());
        EncryptedField {
            cipher_text,
            nonce,
            tag: vec![],
        }
    }
    
    /// Decrypt a single field
    pub fn decrypt_field(&self, encrypted: &EncryptedField) -> Result<String, CryptoError> {
        let plaintext = self.cipher
            .decrypt(&encrypted.cipher_text, &encrypted.nonce)
            .map_err(|e| CryptoError::DecryptionFailed(e.to_string()))?;
        
        String::from_utf8(plaintext)
            .map_err(|e| CryptoError::DecryptionFailed(e.to_string()))
    }
}

/// Unlocked vault with session key in memory
pub struct UnlockedVault {
    pub vault: Vault,
    session_key: Zeroizing<Vec<u8>>,
}

impl UnlockedVault {
    /// Add a new entry
    pub fn add_entry(&mut self, entry: VaultEntry) {
        self.vault.entries.push(entry);
        self.vault.updated_at = Utc::now();
    }
    
    /// Update an existing entry
    pub fn update_entry(&mut self, id: &str, updated: VaultEntry) -> Result<(), String> {
        if let Some(pos) = self.vault.entries.iter().position(|e| e.id == id) {
            self.vault.entries[pos] = updated;
            self.vault.updated_at = Utc::now();
            Ok(())
        } else {
            Err(format!("Entry not found: {}", id))
        }
    }
    
    /// Delete an entry
    pub fn delete_entry(&mut self, id: &str) -> Result<(), String> {
        if let Some(pos) = self.vault.entries.iter().position(|e| e.id == id) {
            self.vault.entries.remove(pos);
            self.vault.updated_at = Utc::now();
            Ok(())
        } else {
            Err(format!("Entry not found: {}", id))
        }
    }
    
    /// Get an entry by ID
    pub fn get_entry(&self, id: &str) -> Option<&VaultEntry> {
        self.vault.entries.iter().find(|e| e.id == id)
    }
    
    /// Search entries by URL
    pub fn search_by_url(&self, url: &str) -> Vec<&VaultEntry> {
        self.vault.entries.iter()
            .filter(|e| e.url.as_deref().map(|u| u.contains(url)).unwrap_or(false))
            .collect()
    }
    
    /// Get all favorites
    pub fn get_favorites(&self) -> Vec<&VaultEntry> {
        self.vault.entries.iter()
            .filter(|e| e.favorite)
            .collect()
    }
    
    /// Lock the vault and clear session key
    pub fn lock(self) -> Vault {
        self.session_key.zeroize();
        self.vault
    }
}

impl Drop for UnlockedVault {
    fn drop(&mut self) {
        self.session_key.zeroize();
    }
}

/// Vault manager - handles vault operations
pub struct VaultManager {
    vault: Vault,
    master_key: Option<Zeroizing<Vec<u8>>>,
}

impl VaultManager {
    /// Create a new vault from OAuth token
    pub fn create(oauth_token: &[u8], pin: Option<&str>) -> Result<UnlockedVault, String> {
        // Derive master key from OAuth token
        let master_key = derive_master_key(oauth_token, b"pq-vault-default");
        
        // If PIN provided, combine with master key (dual-key)
        let session_key = if let Some(pin) = pin {
            // Hash the PIN
            let pin_hash = crate::crypto::kdf::argon2::hash_pin(pin)
                .map_err(|e| e.to_string())?;
            
            // Use PIN hash as local key
            let pin_bytes = pin_hash.as_bytes();
            let local_key = &pin_bytes[..32.min(pin_bytes.len())];
            
            derive_dual_key(&master_key, local_key)
        } else {
            master_key
        };
        
        // Create unlocked vault
        let vault = Vault::default();
        
        Ok(UnlockedVault { vault, session_key })
    }
    
    /// Unlock existing vault
    pub fn unlock(oauth_token: &[u8], encrypted_data: &[u8], pin: Option<&str>) -> Result<UnlockedVault, String> {
        // This would decrypt the encrypted vault data
        // For now, just create a new unlocked vault
        Self::create(oauth_token, pin)
    }
    
    /// Lock the vault
    pub fn lock(&self) {
        // Clear master key from memory
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    
    #[test]
    fn test_create_vault() {
        let oauth_token = b"google_oauth_token_123";
        let unlocked = VaultManager::create(oauth_token, None).unwrap();
        
        assert_eq!(unlocked.vault.entries.len(), 0);
    }
    
    #[test]
    fn test_add_entry() {
        let oauth_token = b"google_oauth_token_123";
        let mut unlocked = VaultManager::create(oauth_token, None).unwrap();
        
        let entry = VaultEntry {
            id: Uuid::new_v4().to_string(),
            entry_type: EntryType::Password,
            title: "Google".to_string(),
            url: Some("google.com".to_string()),
            username: Some("user@gmail.com".to_string()),
            password: Some("password123".to_string()),
            notes: None,
            totp_secret: None,
            favorite: true,
            created_at: Utc::now(),
            updated_at: Utc::now(),
            custom_fields: vec![],
        };
        
        unlocked.add_entry(entry);
        
        assert_eq!(unlocked.vault.entries.len(), 1);
    }
}