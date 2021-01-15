package org.tahoe.lafs.network

data class Error(
    val attribute: String,
    val type: String?,
    val message: String
)
