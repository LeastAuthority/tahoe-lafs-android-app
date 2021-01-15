package org.tahoe.lafs.di

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.tahoe.lafs.network.ApiResponseAdapterFactory
import org.tahoe.lafs.network.TahoeApiService
import org.tahoe.lafs.ui.viewmodel.ScanCodeViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

@JvmField
val appModule = module {
    factory { provideLoggingInterceptor() }
    factory { provideErrorHandlingInterceptor() }
    factory { provideOkHttpClient(get(), get()) }
    single { provideRetrofit(androidContext(), get()) }
    factory { provideApiService(get()) }
    factory { ScanCodeViewModel(get()) }
}

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return loggingInterceptor
}

fun provideErrorHandlingInterceptor(): Interceptor {
    return Interceptor { chain ->
        val request: Request = chain.request()
        val response = chain.proceed(request)
        if (response.code() != 200) {
            Timber.e("Response error: %d %s", response.code(), response.message())
            return@Interceptor response
        }
        response
    }
}

fun provideOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor,
    errorHandlingInterceptor: Interceptor
): OkHttpClient {
    return OkHttpClient()
        .newBuilder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(errorHandlingInterceptor)
        .retryOnConnectionFailure(true)
        .build()
}

//Todo - Replace base_url
fun provideRetrofit(context: Context, okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("base_url")
        .client(okHttpClient)
        .addCallAdapterFactory(ApiResponseAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): TahoeApiService =
    retrofit.create(TahoeApiService::class.java)
