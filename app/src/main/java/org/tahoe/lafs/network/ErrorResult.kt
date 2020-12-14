package org.tahoe.lafs.network

data class ErrorResult(
    val status: Int,
    val errors: List<Error>
)
