package com.quantvault.data.manager.di

import android.app.Application
import android.content.Context
import com.quantvault.core.data.manager.BuildInfoManager
import com.quantvault.core.data.manager.dispatcher.DispatcherManager
import com.quantvault.data.datasource.disk.FlightRecorderDiskSource
import com.quantvault.data.manager.QuantVaultPackageManager
import com.quantvault.data.manager.QuantVaultPackageManagerImpl
import com.quantvault.data.manager.NativeLibraryManager
import com.quantvault.data.manager.NativeLibraryManagerImpl
import com.quantvault.data.manager.appstate.AppStateManager
import com.quantvault.data.manager.appstate.AppStateManagerImpl
import com.quantvault.data.manager.file.FileManager
import com.quantvault.data.manager.file.FileManagerImpl
import com.quantvault.data.manager.flightrecorder.FlightRecorderManager
import com.quantvault.data.manager.flightrecorder.FlightRecorderManagerImpl
import com.quantvault.data.manager.flightrecorder.FlightRecorderWriter
import com.quantvault.data.manager.flightrecorder.FlightRecorderWriterImpl
import com.quantvault.data.repository.ServerConfigRepository
import com.quantvault.network.service.DownloadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

/**
 * Provides managers in the data module.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataManagerModule {

    @Provides
    @Singleton
    fun provideAppStateManager(
        application: Application,
    ): AppStateManager = AppStateManagerImpl(application = application)

    @Provides
    @Singleton
    fun provideQuantVaultPackageManager(
        @ApplicationContext context: Context,
    ): QuantVaultPackageManager = QuantVaultPackageManagerImpl(context = context)

    @Provides
    @Singleton
    fun provideFileManager(
        @ApplicationContext context: Context,
        downloadService: DownloadService,
        dispatcherManager: DispatcherManager,
    ): FileManager = FileManagerImpl(
        context = context,
        downloadService = downloadService,
        dispatcherManager = dispatcherManager,
    )

    @Provides
    @Singleton
    fun provideFlightRecorderManager(
        @ApplicationContext context: Context,
        clock: Clock,
        dispatcherManager: DispatcherManager,
        flightRecorderDiskSource: FlightRecorderDiskSource,
        flightRecorderWriter: FlightRecorderWriter,
    ): FlightRecorderManager = FlightRecorderManagerImpl(
        context = context,
        clock = clock,
        dispatcherManager = dispatcherManager,
        flightRecorderDiskSource = flightRecorderDiskSource,
        flightRecorderWriter = flightRecorderWriter,
    )

    @Provides
    @Singleton
    fun provideFlightRecorderWriter(
        clock: Clock,
        fileManager: FileManager,
        dispatcherManager: DispatcherManager,
        buildInfoManager: BuildInfoManager,
        serverConfigRepository: ServerConfigRepository,
    ): FlightRecorderWriter = FlightRecorderWriterImpl(
        clock = clock,
        fileManager = fileManager,
        dispatcherManager = dispatcherManager,
        buildInfoManager = buildInfoManager,
        serverConfigRepository = serverConfigRepository,
    )

    @Provides
    @Singleton
    fun provideNativeLibraryManager(): NativeLibraryManager = NativeLibraryManagerImpl()
}





