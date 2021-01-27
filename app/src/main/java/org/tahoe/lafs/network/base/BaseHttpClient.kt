package org.tahoe.lafs.network.base

import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.tahoe.lafs.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

abstract class BaseHttpClient(
    private val cache: Cache,
    private val converterFactory: GsonConverterFactory
) {

    private val httpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .cache(cache)
            .readTimeout(60, TimeUnit.SECONDS)

        // add interceptors from respective client classes
        getInterceptors()?.forEach {
            builder.addInterceptor(it)
        }

        // add authenticator if present, required for auto refreshing the token or session cookie
        getAuthenticator()?.let { builder.authenticator(it) }

        // add logging interceptor
        builder.addInterceptor(getLoggingInterceptor())

        builder.build()
    }

    val retrofitClient: Retrofit by lazy {
        Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(converterFactory)
            .baseUrl(getBaseURL())
            .build()
    }

    inline fun <reified T> createService(): T {
        return retrofitClient.create(T::class.java)
    }

    abstract fun getBaseURL(): String

    private fun getLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

    open fun getInterceptors(): List<Interceptor>? = null

    open fun getAuthenticator(): Authenticator? = null
}