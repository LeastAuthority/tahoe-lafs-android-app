package org.tahoe.lafs.utils

import android.view.View
import org.tahoe.lafs.extension.hide
import org.tahoe.lafs.extension.show
import org.tahoe.lafs.network.services.GridNode

object Utils {
    fun hide(vararg views: View) = views.forEach { it.hide() }
    fun show(vararg views: View) = views.forEach { it.show() }
}