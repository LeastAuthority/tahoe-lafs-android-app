package org.tahoe.lafs.network.base

sealed class Resource<T> {
    /**
     * This method can be used to transform success server response from one
     * data model to other data model, using a transform lambda passed..
     */
    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Loading -> Loading()
            is Success -> Success(transform(this.data))
            is Failure -> Failure(this.statusCode, this.error)
        }
    }


    class Loading<T> : Resource<T>()

    data class Success<T>(val data: T) : Resource<T>()

    data class Failure<T>(val statusCode: FailureStatusCode, val error: BaseError) : Resource<T>()
}

/**
 * Define the error object as per the need.
 */
data class BaseError(
    val errorCode: Int,
    val message: String,
    val exception: Exception = Exception()
)

/**
 * Rest API errors broadly classified into three categories..
 * more categories can be added
 */
enum class FailureStatusCode {
    // Generally used to represent the error codes 4**
    CLIENT_ERROR,

    // Generally used to represent the error codes 5**
    SERVER_ERROR,

    // Any other error code
    OTHER
}