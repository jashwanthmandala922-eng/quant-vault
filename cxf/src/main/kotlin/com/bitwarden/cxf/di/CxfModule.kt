package com.quantvault.cxf.di

import com.quantvault.cxf.parser.CredentialExchangePayloadParser
import com.quantvault.cxf.parser.CredentialExchangePayloadParserImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json

/**
 * Provides dependencies from the CXF module.
 */
@Module
@InstallIn(SingletonComponent::class)
object CxfModule {

    @Provides
    fun provideCredentialExchangePayloadParser(
        json: Json,
    ): CredentialExchangePayloadParser = CredentialExchangePayloadParserImpl(
        json = json,
    )
}




