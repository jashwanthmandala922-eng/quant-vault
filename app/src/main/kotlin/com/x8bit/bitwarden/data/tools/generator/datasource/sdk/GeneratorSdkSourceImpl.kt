package com.x8bit.bitwarden.data.tools.generator.datasource.sdk

import com.quantvault.generators.PassphraseGeneratorRequest
import com.quantvault.generators.PasswordGeneratorRequest
import com.quantvault.generators.UsernameGeneratorRequest
import com.quantvault.sdk.GeneratorClients
import com.x8bit.bitwarden.data.platform.datasource.sdk.BaseSdkSource
import com.x8bit.bitwarden.data.platform.manager.SdkClientManager

/**
 * Implementation of [GeneratorSdkSource] that delegates password generation.
 *
 * @property sdkClientManager The [SdkClientManager] used to retrieve an instance of the
 * [GeneratorClients] provided by the Quant Vault SDK.
 */
class GeneratorSdkSourceImpl(
    sdkClientManager: SdkClientManager,
) : BaseSdkSource(sdkClientManager = sdkClientManager),
    GeneratorSdkSource {

    override suspend fun generatePassword(
        request: PasswordGeneratorRequest,
    ): Result<String> = runCatchingWithLogs {
        useClient { generators().password(request) }
    }

    override suspend fun generatePassphrase(
        request: PassphraseGeneratorRequest,
    ): Result<String> = runCatchingWithLogs {
        useClient { generators().passphrase(request) }
    }

    override suspend fun generatePlusAddressedEmail(
        request: UsernameGeneratorRequest.Subaddress,
    ): Result<String> = runCatchingWithLogs {
        useClient { generators().username(request) }
    }

    override suspend fun generateCatchAllEmail(
        request: UsernameGeneratorRequest.Catchall,
    ): Result<String> = runCatchingWithLogs {
        useClient { generators().username(request) }
    }

    override suspend fun generateRandomWord(
        request: UsernameGeneratorRequest.Word,
    ): Result<String> = runCatchingWithLogs {
        useClient { generators().username(request) }
    }

    override suspend fun generateForwardedServiceEmail(
        request: UsernameGeneratorRequest.Forwarded,
    ): Result<String> = runCatchingWithLogs {
        useClient { generators().username(request) }
    }
}




