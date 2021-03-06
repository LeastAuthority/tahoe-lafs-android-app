package org.tahoe.lafs.extension

import android.content.Context
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
            if (list[list.count() - 1] == EMPTY) {
                return list[list.count() - 2]
            } else {
                return list[list.count() - 1]
            }
        } else if (list.count() == 2)
            if (list[list.count() - 1] == EMPTY) {
                return list[0]
            } else {
                return list[1]
            }
    }
    return modifiedString
}

/**
 * Scanned URL format: http://192.168.43.253:8089 URI:DIR2:wbnn6tzynjrnq6hpjnodkmef34:mm5vu33lw3escdkgw3z65tr5suf2q7xiw66s5qmpgm
 */
fun String.getBaseUrl(): String {
    if (this.isNotBlank()) {
        val baseUrl = this.split(" ")[0]
        if (baseUrl.isNotEmpty()) {
            Timber.d("Full parse URL = $baseUrl")
            return baseUrl
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

    return if (lastUpdatedTime < ONE_MINUTE) {
        context.getString(R.string.last_updated_just_now)
    } else if (lastUpdatedTime in ONE_MINUTE until ONE_HOUR) {
        val minutes = lastUpdatedTime / ONE_MINUTE
        context.resources.getQuantityString(
            R.plurals.last_updated_minutes_text,
            minutes.toInt(),
            minutes
        )
    } else if (lastUpdatedTime in ONE_HOUR until ONE_DAY) {
        val hours = lastUpdatedTime / ONE_HOUR
        context.resources.getQuantityString(R.plurals.last_updated_hours_text, hours.toInt(), hours)
    } else {
        val days = lastUpdatedTime / ONE_DAY
        context.resources.getQuantityString(R.plurals.last_updated_days_text, days.toInt(), days)
    }
}
