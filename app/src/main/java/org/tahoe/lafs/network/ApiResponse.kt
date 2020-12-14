package org.tahoe.lafs.network

import java.io.IOException

sealed class ApiResponse<out T : Any, out U : Any> {
    /**
     * Response Success with Body
     */
    data class Success<T : Any>(val body: T) : ApiResponse<T, Nothing>()

    /**
     * Response Failure with body
     */
    data class ApiError<U : Any>(val body: U, val code: Int) : ApiResponse<Nothing, U>()

    /**
     * Network Error
     */
    data class NetworkError(val error: IOException) : ApiResponse<Nothing, Nothing>()

    /**
     * Example, Json Parsing Error
     */
    data class UnknownError(val error: Throwable?) : ApiResponse<Nothing, Nothing>()
}
