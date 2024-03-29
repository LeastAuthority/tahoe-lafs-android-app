package org.tahoe.lafs.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.get
import org.tahoe.lafs.extension.showFullScreenOverStatusBar
import org.tahoe.lafs.model.QRCodeContents
import org.tahoe.lafs.ui.home.HomeActivity
import org.tahoe.lafs.ui.onboarding.StartActivity
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.Constants.SPLASH_TIME_OUT
import org.tahoe.lafs.utils.SharedPreferenceKeys
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : FragmentActivity() {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showFullScreenOverStatusBar()

        Handler(Looper.getMainLooper()).postDelayed({
            val scannerUrl = preferences.get(SharedPreferenceKeys.SCANNER_URL, EMPTY)
            val scannerToken = preferences.get(SharedPreferenceKeys.SCANNER_TOKEN, EMPTY)

            QRCodeContents.parseContents("$scannerUrl $scannerToken").fold({
                startActivity(Intent(this, HomeActivity::class.java))
            }, {
                startActivity(Intent(this, StartActivity::class.java))
            })
            overridePendingTransition(0, 0)
            finish()
        }, SPLASH_TIME_OUT)
    }
}