package ru.madbrains.inspection.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_navigation_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.data.network.IAuthenticator
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.colors
import ru.madbrains.inspection.extensions.strings
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
    private val syncViewModel: SyncViewModel by viewModel()
    private val logoutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mainViewModel.clearDataAndNavToAuth()
        }
    }

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
        mainViewModel.showSnackBar.observe(this, EventObserver {
            showSnackBar(it)
        })
        mainViewModel.showExitDialog.observe(this, EventObserver {
            showExitDialog()
        })
        syncViewModel.openSyncDialog.observe(this, EventObserver {
            navController.navigate(R.id.to_btSyncPanelFragment)
        })
        syncViewModel.showSnackBar.observe(this, EventObserver {
            showSnackBar(resources.getString(it))
        })
        syncViewModel.globalProgress.observe(this, Observer {
            progressView.changeVisibility(it)
        })
        syncViewModel.initAction(
            externalCacheDir, getExternalFilesDir("")
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, IntentFilter(
            IAuthenticator.ACTION_LOGOUT))
    }

    private fun setupMenu() {
        tvUsername.text = mainViewModel.username

        llMarks.isVisible = mainViewModel.isAdmin || mainViewModel.isCreator
        llSettings.isVisible = mainViewModel.isAdmin

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

    private fun showExitDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setMessage(strings[R.string.fragment_dialog_exit_app])
            setPositiveButton(
                strings[R.string.exit]
            ) { _, _ ->
                mainDrawer.closeDrawer(GravityCompat.START)
                mainViewModel.logout()
            }
            setNegativeButton(strings[R.string.fragment_dialog_btn_cancel]) { _, _ ->
                mainDrawer.closeDrawer(GravityCompat.START)
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun showSnackBar(text: String) {
        val snackBar = Snackbar
            .make(coordinatorLayoutMain, text, Snackbar.LENGTH_SHORT)
            .setAction(strings[R.string.proceed], View.OnClickListener {

            })
        snackBar.setTextColor(colors[R.color.textWhite])
        snackBar.setActionTextColor(colors[R.color.accidentDark])
        snackBar.setBackgroundTint(colors[R.color.colorPrimaryDark])
        snackBar.show()
    }

    private fun startAuthActivity() {
        AuthorizationActivity.start(this)
        this.finish()
    }
}