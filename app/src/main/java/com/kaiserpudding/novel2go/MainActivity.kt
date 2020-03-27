package com.kaiserpudding.novel2go

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.kaiserpudding.novel2go.download.*

class MainActivity : AppCompatActivity(),
    NewDownloadFragment.OnDownloadInteractionListener,
    DownloadFragment.OnListFragmentInteractionListener,
    SelectDownloadFragment.OnListFragmentInteractionListener{

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_layout)
        val host =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = host.navController

        supportActionBar?.setDisplayShowHomeEnabled(true)
        drawerLayout = findViewById(R.id.drawer_layout)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawerLayout.closeDrawers()

            when (item.itemId) {
                R.id.home -> {
                    navController.popBackStack(R.id.fragment_download, false)
                }
                R.id.settings -> {
                    navController.navigate(R.id.action_global_settingsFragment)
                }
            }
            true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onNewDownloadInteraction() {
        navController.navigate(
            DownloadFragmentDirections.actionDownloadFragmentToFragmentDownload()
        )
    }

    override fun toSelectDownloads(url: String, waitTime: Int) {
        navController.navigate(
            NewDownloadFragmentDirections.actionFragmentDownloadToSelectDownloadFragment(url, waitTime)
        )
    }

    override fun onStartDownload() {
        navController.popBackStack()
    }

    override fun onSelectFinish() {
        navController.popBackStack()
        navController.popBackStack()
    }
}
