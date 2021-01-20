package org.tahoe.lafs.network.services

import retrofit2.http.POST
import retrofit2.http.Url

interface GridSyncApiService {

    @POST
    suspend fun getMagicFolder(@Url url: String): Unit
}