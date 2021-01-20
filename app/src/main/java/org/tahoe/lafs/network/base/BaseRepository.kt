package org.tahoe.lafs.network.base

import javax.inject.Inject

abstract class BaseRepository {

    @Inject
    protected lateinit var networkUtils: NetworkUtils

    @Throws(NoInternetException::class)
    protected fun ensureInternetConnection() {
        if (!networkUtils.isConnected()) throw NoInternetException()
    }
}
