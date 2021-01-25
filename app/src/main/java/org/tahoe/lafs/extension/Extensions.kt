package org.tahoe.lafs.extension

import org.tahoe.lafs.utils.Constants.COLLECTIVE_TEXT
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.Constants.SUBFOLDER_SUFFIX
import org.tahoe.lafs.utils.Constants.TYPE_JSON
import org.tahoe.lafs.utils.Constants.URI_SCHEMA
import timber.log.Timber
import java.net.URL

/**
 * Any generic extension functions which are not part of the core ktx libraries can be
 * added here.
 */

fun String.getShortCollectiveFolderName(): String {
    val modifiedString = this.replace(COLLECTIVE_TEXT, EMPTY)
    if (modifiedString.endsWith(SUBFOLDER_SUFFIX, true)) {
        return modifiedString.replace(SUBFOLDER_SUFFIX, EMPTY)
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
