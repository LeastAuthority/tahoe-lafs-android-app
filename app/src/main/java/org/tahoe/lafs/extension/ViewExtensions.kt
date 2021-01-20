package org.tahoe.lafs.extension

import android.view.View

/**
 * Any view related extension functions which are not part of the core ktx libraries can be
 * added here.
 */

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.dpToPx(dpValue: Int): Int {
    return (dpValue * resources.displayMetrics.density).toInt()
}
