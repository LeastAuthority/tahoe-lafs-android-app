package org.tahoe.lafs

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TahoeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // plant Timber debug tree
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}




