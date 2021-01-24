package org.tahoe.lafs.utils

import android.view.View
import org.tahoe.lafs.extension.hide
import org.tahoe.lafs.extension.show
import java.net.URL

object Utils {
    fun hide(vararg views: View) = views.forEach { it.hide() }

    fun show(vararg views: View) = views.forEach { it.show() }

    fun getGridSyncEndPointIp(scannedUrl: String): String {
        if (scannedUrl.isNotBlank()) {
            val fullUrl = scannedUrl.split(" ")[0]
            if (fullUrl.isNotEmpty()) {
                val ipAddress = URL(fullUrl).host
                if (ipAddress.isNotEmpty()) {
                    return ipAddress
                }
            }
        }
        return "0.0.0.0"
    }
}