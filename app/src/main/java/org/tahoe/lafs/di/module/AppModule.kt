package org.tahoe.lafs.di.module

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import java.io.File
import javax.inject.Singleton

/**
 * This module provides all the application level dependency.
 * mostly all singletons
 */

const val SHARED_PREFERENCES_NAME = "org_tahoe_lafs_shared_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesGson() = Gson()


    @Provides
    @Singleton
    fun providesCache(cacheFile: File): Cache = Cache(
        cacheFile,
        50L * 1024L * 1024L
    )

    @Provides
    @Singleton
    fun providesCacheFile(@ApplicationContext context: Context): File {
        val cacheFile = File(context.cacheDir, "http_cache")
        cacheFile.mkdirs()
        return cacheFile
    }

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
}