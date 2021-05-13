package org.tahoe.lafs.di.module

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.tahoe.lafs.network.services.GridSyncApiClient
import org.tahoe.lafs.network.services.GridSyncApiService
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * This module provides the implementations of the retrofit service interfaces
 * can be provided here.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun providesGsonConverterFactory(gson: Gson) = GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun providesGridSyncApiService(gridSyncApiClient: GridSyncApiClient): GridSyncApiService =
        gridSyncApiClient.createService()
}