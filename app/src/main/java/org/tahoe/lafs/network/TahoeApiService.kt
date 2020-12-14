package org.tahoe.lafs.network

import okhttp3.Request
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TahoeApiService {

    @GET("uri/URI:{token}?type=json")
    suspend fun getMagicFolder(
        @Header("type") type: String,
        @Path(value = "token") shopId: String
    ): ApiResponse<Data<Request>, ErrorResult>
}
