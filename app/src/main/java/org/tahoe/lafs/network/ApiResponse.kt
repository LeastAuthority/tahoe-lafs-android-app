package org.tahoe.lafs.network

import java.io.IOException

sealed class ApiResponse<out T : Any, out U : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T) : ApiResponse<T, Nothing>()

    /**
     * Failure response with body
     */
    data class ApiError<U : Any>(val body: U, val code: Int) : ApiResponse<Nothing, U>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : ApiResponse<Nothing, Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable?) : ApiResponse<Nothing, Nothing>()
}
