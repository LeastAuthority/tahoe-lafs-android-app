package org.tahoe.lafs.network.services

import android.content.SharedPreferences
import okhttp3.Cache
import org.tahoe.lafs.network.base.BaseHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GridSyncApiClient @Inject constructor(
    cache: Cache,
    converterFactory: GsonConverterFactory,
    preferences: SharedPreferences
) : BaseHttpClient(cache, converterFactory, preferences) {

    // As we are calling API interfaces based on dynamic urls, setting default base url as localhost
    override fun getBaseURL(): String = "https://localhost"
}