#!/bin/bash

# Script to update SDK version in gradle/libs.versions.toml
# Usage: ./scripts/update-sdk-version.sh <sdk-package> <sdk-version>

set -euo pipefail

if [ $# -lt 2 ]; then
    echo "Usage: $0 <sdk-package> <sdk-version>"
    echo "Example: $0 com.quantvault:sdk-android 1.0.0-2586-20e3dfa6"
    echo "Example: $0 com.quantvault:sdk-android.dev 1.0.0-2577-fix-wasm-import"
    exit 1
fi

SDK_PACKAGE="$1"
SDK_VERSION="$2"
TOML_FILE="gradle/libs.versions.toml"

echo "Updating SDK in $TOML_FILE..."
echo "  Package: $SDK_PACKAGE"
echo "  Version: $SDK_VERSION"

sed -i.bak "s/Quant VaultSdk = \".*\"/Quant VaultSdk = \"$SDK_VERSION\"/" "$TOML_FILE"

if [ "$SDK_PACKAGE" != "com.quantvault:sdk-android" ]; then
    sed -i.bak "s|Quant Vault-sdk = { module = \".*\"|Quant Vault-sdk = { module = \"$SDK_PACKAGE\", version.ref = \"Quant VaultSdk\"|" "$TOML_FILE"
fi

echo "Updated:"
grep -n "Quant VaultSdk\|Quant Vault-sdk" "$TOML_FILE"
