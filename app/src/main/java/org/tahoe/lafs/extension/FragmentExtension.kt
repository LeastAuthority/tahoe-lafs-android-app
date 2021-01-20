package org.tahoe.lafs.extension

import androidx.fragment.app.Fragment

/**
 * Any Fragment related extension functions which are not part of the core ktx libraries can be
 * added here.
 */

fun Fragment.hideKeyBoard() {
    activity?.hideKeyboard()
}

fun Fragment.performBackPress() {
    hideKeyBoard()
    activity?.onBackPressed()
}