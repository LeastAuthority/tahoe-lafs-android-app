package org.tahoe.lafs.ui.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.remove
import org.tahoe.lafs.extension.showFullScreenOverStatusBar
import org.tahoe.lafs.ui.onboarding.StartActivity
import org.tahoe.lafs.ui.onboarding.StartActivity.Companion.SCANNER_FRAGMENT
import org.tahoe.lafs.ui.onboarding.StartActivity.Companion.START_FRAGMENT
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        showFullScreenOverStatusBar()
        setUpToolbar()
        startNavigationJourney()
    }

    private fun setUpToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun startNavigationJourney() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.home_navhost_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.home_nav_graph)
        navHostFragment.navController.graph = graph
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun setToolbarText(title: String, subTitle: String) {
        txtToolbarTitle.text = title
        txtToolbarDesc.text = subTitle
    }

    fun setNavigationViewDetails(gateway: String) {
        val headerView = navigationView.getHeaderView(0)
        val txtGatewayDetails = headerView.findViewById<AppCompatTextView>(R.id.txtGatewayDetails)
        txtGatewayDetails.text = getString(R.string.connected_gateway_placeholder, gateway)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.start_navhost_fragment) as NavHostFragment
        navHostFragment.childFragmentManager.primaryNavigationFragment.run {
            this?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.disconnect -> {
                // Handle disconnect
                preferences.remove(SCANNER_URL)
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }

            R.id.scanCode -> {
                preferences.remove(SCANNER_URL)
                val intent = Intent(this, StartActivity::class.java)
                intent.putExtra(START_FRAGMENT, SCANNER_FRAGMENT)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }

            R.id.testItem1 -> {
            }

            R.id.testItem2 -> {
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}