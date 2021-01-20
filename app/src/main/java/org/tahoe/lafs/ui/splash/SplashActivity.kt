package org.tahoe.lafs.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.showFullScreenOverStatusBar
import org.tahoe.lafs.ui.onboarding.StartActivity
import org.tahoe.lafs.utils.Constants.SPLASH_TIME_OUT

@AndroidEntryPoint
class SplashActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showFullScreenOverStatusBar()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, StartActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }, SPLASH_TIME_OUT)
    }
}