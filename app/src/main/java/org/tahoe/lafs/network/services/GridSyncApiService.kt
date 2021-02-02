package org.tahoe.lafs.network.services

import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface GridSyncApiService {

    @GET
    suspend fun getFolderStructure(@Url url: String): JsonElement

    @GET
    suspend fun downloadFile(@Url url: String): ResponseBody
}