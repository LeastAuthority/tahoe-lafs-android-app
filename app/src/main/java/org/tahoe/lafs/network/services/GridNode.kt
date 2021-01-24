package org.tahoe.lafs.network.services

data class GridNode(
    val verifyUri: String,
    val roUri: String,
    val rwUri: String,
    val linkMoTime: Double,
    val linkCrTime: Double,
    val mutable: Boolean,
    val name: String
)
