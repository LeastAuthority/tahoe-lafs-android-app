package org.tahoe.lafs.extension

import android.app.Activity
import android.view.inputmethod.InputMethodManager

/**
 * Any Activity related extension functions which are not part of the core ktx libraries can be
 * added here.
 */

fun Activity.hideKeyboard() {
    val inputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}