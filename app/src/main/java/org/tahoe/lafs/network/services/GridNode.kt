package org.tahoe.lafs.network.services

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val MUTABLE = "mutable"
const val VERIFY_URI = "verify_uri"
const val RO_URI = "ro_uri"
const val RW_URI = "rw_uri"
const val METADATA = "metadata"
const val TAHOE = "tahoe"
const val LINK_MO_TIME = "linkmotime"
const val LINK_CR_TIME = "linkcrtime"
const val CHILDREN = "children"
const val CHK = "CHK"
const val FORMAT = "format"
const val LAST_DOWNLOAD_TIME = "last_downloaded_timestamp"
const val SIZE = "size"
const val DELETED = "deleted"

@Parcelize
data class GridNode(
    val verifyUri: String,
    val roUri: String,
    val rwUri: String,
    val linkMoTime: Double,
    val linkCrTime: Double,
    val mutable: Boolean,
    val name: String,
    val isDir: Boolean,
    val size: Long = 0,
    val deleted: Boolean = false,
    val lastDownloadedTimestamp: Double = 0.0,
    val format: String = CHK

) : Parcelable
