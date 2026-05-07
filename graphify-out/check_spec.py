from networkx.readwrite import json_graph
import json

G = json_graph.node_link_graph(json.load(open('graphify-out/graph.json')), edges='links')

# Features from SPEC.md nuclear security
spec_terms = [
    'argon2id',      # Argon2id - implemented (8 found)
    'shamir',        # Shamir Secret Sharing - MISSING
    'opa',           # OPAQUE protocol - found (likely from SDK)
    'pqc',          # Post-quantum crypto - MISSING
    'postquant',     # Post-quantum - MISSING
    'mlkem',         # ML-KEM (Kyber) - MISSING
    'mldsa',        # ML-DSA (Dilithium) - MISSING
    'x25519',       # X25519 key exchange - MISSING
    'ble',          # Bluetooth LE transfer - MISSING
    'secure_wipe',   # Secure memory wipe - MISSING
    'nonce',        # Nonce management - found (5)
]

print("=== NUCLEAR SECURITY SPEC ===Status===")
for term in spec_terms:
    count = sum(1 for n, d in G.nodes(data=True) if term in d.get('label', '').lower())
    status = f"FOUND ({count})" if count > 0 else "MISSING"
    print(f"  {term:15s}: {status}")