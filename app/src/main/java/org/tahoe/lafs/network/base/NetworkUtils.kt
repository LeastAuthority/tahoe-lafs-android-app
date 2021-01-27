package org.tahoe.lafs.network.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkUtils @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ConnectivityManager.NetworkCallback() {

    init {
        initNetworkChangeListener()
    }

    private val _networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    /**
     * Any UI component which requires connectivity change updates can subscribe to this livedata.
     */
    val networkStateLiveData: LiveData<NetworkState>
        get() = _networkStateLiveData

    private lateinit var connectivityManager: ConnectivityManager

    private fun initNetworkChangeListener() {
        connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(this)
        } else {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), this)
        }
    }

    fun isConnected(): Boolean {
        // check if the network state live data has value, if so return the value in it
        // as its getting updated by connectivity callback methods.
        if (_networkStateLiveData.value != null)
            return _networkStateLiveData.value == NetworkState.CONNECTED

        // in case network state live data is not yet initialized,
        // do the below check and return value accordingly,
        // This happens when t the first callback of network changes hasn't come yet.

        // Retrieve current status of connectivity
        connectivityManager.allNetworks.forEach { network ->
            val networkCapability = connectivityManager.getNetworkCapabilities(network)

            networkCapability?.let {
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                    return true
            }
        }

        return false
    }

    override fun onAvailable(network: Network) {
        _networkStateLiveData.postValue(NetworkState.CONNECTED)
    }

    override fun onLost(network: Network) {
        _networkStateLiveData.postValue(NetworkState.DISCONNECTED)
    }

    enum class NetworkState {
        CONNECTED, DISCONNECTED
    }
}