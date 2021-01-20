package org.tahoe.lafs.ui.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.showFullScreenOverStatusBar
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        showFullScreenOverStatusBar()
        startNavigationJourney()
    }

    private fun startNavigationJourney() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.home_navhost_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.home_nav_graph)
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