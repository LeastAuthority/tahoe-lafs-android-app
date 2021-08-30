package org.tahoe.lafs.extension

import android.content.Context
import android.os.Build
import org.tahoe.lafs.R
import org.tahoe.lafs.utils.Constants.COLLECTIVE_TEXT
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.Constants.ONE_DAY
import org.tahoe.lafs.utils.Constants.ONE_HOUR
import org.tahoe.lafs.utils.Constants.ONE_MINUTE
import org.tahoe.lafs.utils.Constants.SUBFOLDER_SUFFIX
import org.tahoe.lafs.utils.Constants.TYPE_JSON
import org.tahoe.lafs.utils.Constants.URI_SCHEMA
import timber.log.Timber
import java.net.URL
import java.net.MalformedURLException
import java.util.*

/**
 * Any generic extension functions which are not part of the core ktx libraries can be
 * added here.
 */

fun String.getShortCollectiveFolderName(): String {
    val modifiedString = this.replace(COLLECTIVE_TEXT, EMPTY)
    if (modifiedString.contains(SUBFOLDER_SUFFIX, true)) {
        val list = modifiedString.split(SUBFOLDER_SUFFIX)
        if (list.count() > 2) {
            return if (list[list.count() - 1] == EMPTY) {
                list[list.count() - 2]
            } else {
                list[list.count() - 1]
            }
        } else if (list.count() == 2)
            return if (list[list.count() - 1] == EMPTY) {
                list[0]
            } else {
                list[1]
            }
    }
    return modifiedString
}

/**
 * Scanned URL format
 * https://10.137.0.16:58483/XOu4O4wM0UMQhRErgJ4GAZgQ9hTeh68m LS0tLSsdfsdfsdfsf==
 **/
fun String.getBaseUrl(): String {
    if (this.isNotBlank()) {
        val fullUrl = this.split(" ")[0]
        if (fullUrl.isNotEmpty()) {
            Timber.d("Full parse URL = $fullUrl")
            val url = URL(fullUrl)
            return "${url.protocol}://${url.host}:${url.port}"
        }
    }
    return EMPTY
}

fun String.getEndPointIp(): String {
    if (this.isNotBlank()) {
        val fullUrl = this.split(" ")[0]
        if (fullUrl.isNotEmpty()) {
            Timber.d("Full parse URL = $fullUrl")
            val ipAddress = URL(fullUrl).host
            if (ipAddress.isNotEmpty()) {
                return ipAddress
            }
        }
    }
    return "0.0.0.0"
}

fun String.formattedFolderUrl() = this.replace(" ", URI_SCHEMA).plus(TYPE_JSON)

fun Long.getLastUpdatedText(context: Context): String {
    val lastUpdatedTime = Date().time - this

    return when {
        lastUpdatedTime < ONE_MINUTE -> {
            context.getString(R.string.last_updated_just_now)
        }
        lastUpdatedTime in ONE_MINUTE until ONE_HOUR -> {
            val minutes = lastUpdatedTime / ONE_MINUTE
            context.resources.getQuantityString(
                R.plurals.last_updated_minutes_text,
                minutes.toInt(),
                minutes
            )
        }
        lastUpdatedTime in ONE_HOUR until ONE_DAY -> {
            val hours = lastUpdatedTime / ONE_HOUR
            context.resources.getQuantityString(
                R.plurals.last_updated_hours_text,
                hours.toInt(),
                hours
            )
        }
        else -> {
            val days = lastUpdatedTime / ONE_DAY
            context.resources.getQuantityString(
                R.plurals.last_updated_days_text,
                days.toInt(),
                days
            )
        }
    }
}

fun String.getRawCertificate(): String {
    val decodedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Base64.getMimeDecoder().decode(this)
    } else {
        android.util.Base64.decode(this, android.util.Base64.DEFAULT)
    }
    return String(decodedBytes)
}
