package org.tahoe.lafs.network.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    /**
     * Method used to perform async task which takes a lot of time
     * such as rest service.
     * This can emit 3 states, LOADING, SUCCESS and FAILURE.
     */
    protected fun <T> getResult(asyncAction: suspend () -> T, onResult: (Resource<T>) -> Unit) {
        viewModelScope.launch {
            try {
                onResult(Resource.Loading())
                val data = asyncAction()
                onResult(Resource.Success(data))
            } catch (exception: Exception) {
                Timber.e(exception)
                val failure = parseException<T>(exception)
                onResult(failure)
            }
        }
    }

    /**
     * Method generally used to perform async tasks related to database.
     * mostly DAO operations.
     */
    protected fun <T> performTask(asyncAction: suspend () -> T, onResult: (T) -> Unit) {
        viewModelScope.launch {
            val result = asyncAction()
            onResult(result)
        }
    }

    private fun <T> parseException(exception: Exception): Resource.Failure<T> {
        // Write custom parse exception logic depending on API structure.
        return Resource.Failure(FailureStatusCode.CLIENT_ERROR, BaseError(502, "Error"))
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}