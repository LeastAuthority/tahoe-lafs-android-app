package org.tahoe.lafs.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.showFullScreenOverStatusBar

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    companion object {
        const val START_FRAGMENT = "start_fragment"
        const val SCANNER_FRAGMENT = "scanner_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        showFullScreenOverStatusBar()
        startNavigationJourney()
    }

    private fun startNavigationJourney() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.start_navhost_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.start_nav_graph)

        if (intent.getStringExtra(START_FRAGMENT) == SCANNER_FRAGMENT) {
            graph.setStartDestination(R.id.scannerFragment)
        } else {
            graph.setStartDestination(R.id.introFragment)
        }

        navHostFragment.navController.graph = graph
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.start_navhost_fragment) as NavHostFragment
        navHostFragment.childFragmentManager.primaryNavigationFragment.run {
            this?.onActivityResult(requestCode, resultCode, data)
        }
    }
}