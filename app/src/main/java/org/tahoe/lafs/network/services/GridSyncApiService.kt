package org.tahoe.lafs.network.services

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Url

interface GridSyncApiService {

    @GET
    suspend fun getFolderStructure(@Url url: String): JsonElement
}