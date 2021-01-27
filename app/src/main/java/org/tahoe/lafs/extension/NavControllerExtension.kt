package org.tahoe.lafs.extension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import org.tahoe.lafs.R
import timber.log.Timber

fun NavController.navigateWithClearStack(
    navGraphId: Int,
    @IdRes destId: Int,
    args: Bundle? = null,
    anim: Boolean = true,
    inclusive: Boolean = true
) {
    val builder = NavOptions.Builder()
        .setPopUpTo(navGraphId, inclusive)
        .setLaunchSingleTop(true)
    if (anim) {
        try {
            builder.setEnterAnim(R.anim.from_right)
                .setExitAnim(R.anim.to_left)
                .setPopEnterAnim(R.anim.from_left)
                .setPopExitAnim(R.anim.to_right)
        } catch (e: IllegalArgumentException) {
            Timber.e("Multiple navigation attempts handled ${e.message}")
        }
    }
    this.navigate(
        destId,
        args,
        builder.build()
    )
}

fun NavController.navigateWithAnim(directions: NavDirections) {
    try {
        this.navigate(
            directions,
            NavOptions.Builder()
                .setEnterAnim(R.anim.from_right)
                .setExitAnim(R.anim.to_left)
                .setPopEnterAnim(R.anim.from_left)
                .setPopExitAnim(R.anim.to_right).build()
        )
    } catch (e: IllegalArgumentException) {
        Timber.e("Multiple navigation attempts handled ${e.message}")
    }
}

fun NavController.navigateWithAnim(@IdRes destId: Int, args: Bundle? = null) {
    try {
        this.navigate(
            destId,
            args,
            NavOptions.Builder()
                .setEnterAnim(R.anim.from_left)
                .setExitAnim(R.anim.to_left)
                .setPopEnterAnim(R.anim.from_left)
                .setPopExitAnim(R.anim.to_right).build()
        )
    } catch (e: IllegalArgumentException) {
        Timber.e("Multiple navigation attempts handled ${e.message}")
    }
}

fun NavController.isPresentInBackStack(id: Int): Boolean {
    return try {
        getBackStackEntry(id)
        true
    } catch (exception: IllegalArgumentException) {
        Timber.d("NavController id not present, ${exception.message}")
        false
    }
}
