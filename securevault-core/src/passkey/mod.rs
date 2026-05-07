// Passkey (WebAuthn/FIDO2) support
// Hybrid approach: ECDSA (P-256) + ML-DSA-65

use std::collections::HashMap;
use thiserror::Error;

#[derive(Error, Debug)]
pub enum PasskeyError {
    #[error("Registration failed: {0}")]
    Registration(String),
    #[error("Authentication failed: {0}")]
    Authentication(String),
    #[error("Invalid credential: {0}")]
    InvalidCredential(String),
}

/// Passkey credential
#[derive(Debug, Clone)]
pub struct PasskeyCredential {
    pub id: String,
    pub relying_party_id: String,
    pub user_id: String,
    pub public_key: Vec<u8>,
    pub created_at: i64,
    pub last_used: Option<i64>,
}

/// Passkey relying party
#[derive(Debug)]
pub struct PasskeyRP {
    pub id: String,
    pub name: String,
}

impl PasskeyRP {
    pub fn new(id: String, name: String) -> Self {
        Self { id, name }
    }
}

/// Passkey user
#[derive(Debug)]
pub struct PasskeyUser {
    pub id: String,
    pub name: String,
    pub display_name: Option<String>,
}

/// Challenge for passkey operations
#[derive(Debug, Clone)]
pub struct PasskeyChallenge {
    pub relying_party_id: String,
    pub user_id: String,
    pub challenge: Vec<u8>,
    pub timeout: u32,
}

/// Passkey attestation
#[derive(Debug, Clone)]
pub enum AttestationConveyancePreference {
    None,
    Indirect,
    Direct,
    Enterprise,
}

/// Passkey registration options
#[derive(Debug)]
pub struct RegistrationOptions {
    pub rp: PasskeyRP,
    pub user: PasskeyUser,
    pub challenge: Vec<u8>,
    pub pub_key_cred_params: Vec<PubKeyCredentialType>,
    pub timeout: u32,
    pub attestation: AttestationConveyancePreference,
}

impl RegistrationOptions {
    pub fn new(rp: PasskeyRP, user: PasskeyUser, challenge: Vec<u8>) -> Self {
        Self {
            rp,
            user,
            challenge,
            pub_key_cred_params: vec![
                PubKeyCredentialType::new("ECDSA".to_string(), vec![-7]), // ES256
                PubKeyCredentialType::new("RSA".to_string(), vec![-257]), // RS256
            ],
            timeout: 60000,
            attestation: AttestationConveyancePreference::None,
        }
    }
}

/// Public key credential type
#[derive(Debug)]
pub struct PubKeyCredentialType {
    pub alg: String,
    pub id: Vec<i32>,
}

impl PubKeyCredentialType {
    pub fn new(alg: String, id: Vec<i32>) -> Self {
        Self { alg, id }
    }
}

/// Passkey authentication options
#[derive(Debug)]
pub struct AuthenticationOptions {
    pub relying_party_id: String,
    pub challenge: Vec<u8>,
    pub timeout: u32,
    pub user_verification: UserVerificationRequirement,
}

impl AuthenticationOptions {
    pub fn new(relying_party_id: String, challenge: Vec<u8>) -> Self {
        Self {
            relying_party_id,
            challenge,
            timeout: 60000,
            user_verification: UserVerificationRequirement::Preferred,
        }
    }
}

/// User verification requirement
#[derive(Debug, Clone)]
pub enum UserVerificationRequirement {
    Preferred,
    Required,
    Discouraged,
}

/// Passkey credential repository
pub struct PasskeyRepository {
    credentials: HashMap<String, PasskeyCredential>,
}

impl PasskeyRepository {
    pub fn new() -> Self {
        Self {
            credentials: HashMap::new(),
        }
    }
    
    /// Store credential
    pub fn store(&mut self, credential: PasskeyCredential) {
        self.credentials.insert(credential.id.clone(), credential);
    }
    
    /// Get credential by ID
    pub fn get(&self, id: &str) -> Option<&PasskeyCredential> {
        self.credentials.get(id)
    }
    
    /// Get credentials for relying party
    pub fn get_for_rp(&self, rp_id: &str) -> Vec<&PasskeyCredential> {
        self.credentials.values()
            .filter(|c| c.relying_party_id == rp_id)
            .collect()
    }
    
    /// Delete credential
    pub fn delete(&mut self, id: &str) -> bool {
        self.credentials.remove(id).is_some()
    }
    
    /// List all credentials
    pub fn list(&self) -> Vec<&PasskeyCredential> {
        self.credentials.values().collect()
    }
}

impl Default for PasskeyRepository {
    fn default() -> Self {
        Self::new()
    }
}

/// Passkey manager - handles passkey operations
pub struct PasskeyManager {
    repository: PasskeyRepository,
}

impl PasskeyManager {
    pub fn new() -> Self {
        Self {
            repository: PasskeyRepository::new(),
        }
    }
    
    /// Generate registration challenge
    pub fn generate_registration_challenge(&self, rp: &PasskeyRP, user: &PasskeyUser) -> PasskeyChallenge {
        let mut challenge = vec![0u8; 32];
        crate::crypto::rng::SecureRng::fill_bytes(&mut challenge);
        
        PasskeyChallenge {
            relying_party_id: rp.id.clone(),
            user_id: user.id.clone(),
            challenge,
            timeout: 60000,
        }
    }
    
    /// Generate authentication challenge
    pub fn generate_authentication_challenge(&self, rp_id: &str) -> PasskeyChallenge {
        let mut challenge = vec![0u8; 32];
        crate::crypto::rng::SecureRng::fill_bytes(&mut challenge);
        
        PasskeyChallenge {
            relying_party_id: rp_id.to_string(),
            user_id: String::new(),
            challenge,
            timeout: 60000,
        }
    }
    
    /// Store registered credential
    pub fn store_credential(&mut self, credential: PasskeyCredential) {
        self.repository.store(credential);
    }
    
    /// Get available credentials for RP
    pub fn get_credentials(&self, rp_id: &str) -> Vec<&PasskeyCredential> {
        self.repository.get_for_rp(rp_id)
    }
    
    /// Delete credential
    pub fn delete_credential(&mut self, id: &str) -> bool {
        self.repository.delete(id)
    }
}

impl Default for PasskeyManager {
    fn default() -> Self {
        Self::new()
    }
}