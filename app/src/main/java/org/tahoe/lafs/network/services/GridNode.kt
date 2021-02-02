package org.tahoe.lafs.network.services

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

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
    val format: String = CHK,
    var filesList: MutableList<GridNode> = mutableListOf()

) : Parcelable {
    fun getFormattedDescription(): String {
        if (isDir && filesList.isNotEmpty()) {
            return String.format(
                "%d files; %s",
                filesList.count(),
                readableFileSize(getAllFilesSize(filesList))
            )
        } else {
            return readableFileSize(size)
        }
    }

    private fun getAllFilesSize(filesList: MutableList<GridNode>): Long {
        return if (filesList.isEmpty()) {
            0
        } else {
            var totalSize = filesList.sumOf { it.size }
            for (folder in filesList) {
                totalSize += getAllFilesSize(folder.filesList)
            }
            totalSize
        }
    }

    private fun readableFileSize(size: Long): String {
        if (size <= 0) return "0 Bytes"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1000.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1000.0.pow(digitGroups.toDouble()))
            .toString() + " " + units[digitGroups]
    }
}
