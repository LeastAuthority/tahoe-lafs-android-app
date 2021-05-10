package org.tahoe.lafs.network.base

import android.annotation.SuppressLint
import android.content.SharedPreferences
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.tahoe.lafs.BuildConfig
import org.tahoe.lafs.extension.get
import org.tahoe.lafs.extension.getEndPointIp
import org.tahoe.lafs.utils.Constants
import org.tahoe.lafs.utils.DnsOverride
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_TOKEN
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


abstract class BaseHttpClient(
    private val cache: Cache,
    private val converterFactory: GsonConverterFactory,
    private val preferences: SharedPreferences
) {

    companion object {
        @SuppressLint("TrustAllX509TrustManager")
        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                        //TODO Nothing
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                        //TODO Nothing
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                // builder.hostnameVerifier { _, _ -> true }
                builder.hostnameVerifier(hostnameVerifier = HostnameVerifier { _, _ -> true })

                return builder
            } catch (e: Exception) {
                Timber.e(e)
            }

            return OkHttpClient.Builder()
        }
    }

    private val httpClient: OkHttpClient by lazy {
        val hostName = preferences.get(SCANNER_URL, Constants.EMPTY).getEndPointIp()
        val shaKey = preferences.get(SCANNER_TOKEN, Constants.EMPTY)

        val certificatePinner: CertificatePinner = CertificatePinner.Builder()
            .add(hostName, "sha256$shaKey")
            .build()

        val builder = getUnsafeOkHttpClient()
            .certificatePinner(certificatePinner)
            .dns(DnsOverride(Dns.SYSTEM))
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