package com.quantvault.data.repository.di

import com.quantvault.core.data.manager.dispatcher.DispatcherManager
import com.quantvault.data.datasource.disk.ConfigDiskSource
import com.quantvault.data.repository.ServerConfigRepository
import com.quantvault.data.repository.ServerConfigRepositoryImpl
import com.quantvault.network.service.ConfigService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

/**
 * Provides repositories in the data module.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideServerConfigRepository(
        configDiskSource: ConfigDiskSource,
        configService: ConfigService,
        clock: Clock,
        dispatcherManager: DispatcherManager,
    ): ServerConfigRepository =
        ServerConfigRepositoryImpl(
            configDiskSource = configDiskSource,
            configService = configService,
            clock = clock,
            dispatcherManager = dispatcherManager,
        )
}





