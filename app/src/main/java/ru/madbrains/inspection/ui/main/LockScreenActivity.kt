package ru.madbrains.inspection.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_lock_screen.*
import kotlinx.android.synthetic.main.activity_lock_screen.progressView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.colors
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.auth.AuthorizationActivity

class LockScreenActivity : BaseActivity(R.layout.activity_lock_screen) {

    private val lockScreenViewModel: LockScreenViewModel by viewModel()
    private val syncViewModel: SyncViewModel by viewModel()
    private lateinit var navController: NavController

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LockScreenActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController(R.id.nav_host_container)

        lockScreenViewModel.navigateToMain.observe(this, EventObserver {
            MainActivity.start(this)
        })
        lockScreenViewModel.navigateToAuthorization.observe(this, EventObserver {
            AuthorizationActivity.start(this)
            this.finish()
        })
        lockScreenViewModel.progressVisibility.observe(this, Observer {
            progressView.changeVisibility(it)
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
    }

    private fun showSnackBar(text: String) {
        val snackBar = Snackbar
            .make(clLockScreenMain, text, Snackbar.LENGTH_SHORT)
            .setAction(strings[R.string.proceed], View.OnClickListener {

            })
        snackBar.setTextColor(colors[R.color.textWhite])
        snackBar.setActionTextColor(colors[R.color.accidentDark])
        snackBar.setBackgroundTint(colors[R.color.colorPrimaryDark])
        snackBar.show()
    }
}