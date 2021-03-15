package ru.madbrains.inspection.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_navigation_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.auth.AuthorizationActivity

class MainActivity : BaseActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController(R.id.nav_host_container)

        setupMenu()

        mainViewModel.progressVisibility.observe(this, Observer {
            progressView.changeVisibility(it)
        })
        mainViewModel.navigateToMenu.observe(this, EventObserver {
            mainDrawer.openDrawer(GravityCompat.START)
        })
        mainViewModel.navigateToRoutes.observe(this, EventObserver {
            openRoutesGraph()
        })
        mainViewModel.navigateToDefects.observe(this, EventObserver {
            openDefectsGraph()
        })
        mainViewModel.navigateToMarks.observe(this, EventObserver {
            openMarksGraph()
        })
        mainViewModel.navigateToSync.observe(this, EventObserver {
            openSyncGraph()
        })
        mainViewModel.navigateToSettings.observe(this, EventObserver {
            openSettingsGraph()
        })
        mainViewModel.navigateToAuthorization.observe(this, EventObserver {
            startAuthActivity()
        })
    }

    private fun setupMenu() {
        tvUsername.text = mainViewModel.username
        navView.apply {
            llRouteList.setOnClickListener {
                mainViewModel.routesClick()
            }
            llDefectList.setOnClickListener {
                mainViewModel.defectsClick()
            }
            llMarks.setOnClickListener {
                mainViewModel.marksClick()
            }
            llSync.setOnClickListener {
                mainViewModel.syncClick()
            }
            llSettings.setOnClickListener {
                mainViewModel.settingsClick()
            }
            llLogout.setOnClickListener {
                mainViewModel.logoutClick()
            }
        }
    }

    private fun openRoutesGraph() {
        navController.popBackStack(navController.graph.startDestination, true)
        navController.navigate(R.id.graph_routes)
        mainDrawer.closeDrawer(GravityCompat.START)
    }

    private fun openDefectsGraph() {
        navController.popBackStack(navController.graph.startDestination, false)
        navController.navigate(R.id.graph_defects)
        mainDrawer.closeDrawer(GravityCompat.START)
    }

    private fun openMarksGraph() {
        navController.popBackStack(navController.graph.startDestination, false)
        navController.navigate(R.id.graph_marks)
        mainDrawer.closeDrawer(GravityCompat.START)
    }

    private fun openSyncGraph() {
        navController.popBackStack(navController.graph.startDestination, false)
        navController.navigate(R.id.grapg_sync)
        mainDrawer.closeDrawer(GravityCompat.START)
    }

    private fun openSettingsGraph() {
        navController.popBackStack(navController.graph.startDestination, false)
        navController.navigate(R.id.grapg_settings)
        mainDrawer.closeDrawer(GravityCompat.START)
    }

    private fun startAuthActivity() {
        AuthorizationActivity.start(this)
        this.finish()
    }
}