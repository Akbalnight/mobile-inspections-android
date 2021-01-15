package ru.madbrains.inspection.ui.launcher

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.auth.AuthorizationActivity
import ru.madbrains.inspection.ui.main.MainActivity

class LauncherActivity : AppCompatActivity() {

    private val launcherViewModel: LauncherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcherViewModel.launchDestination.observe(this, EventObserver { destination ->
            when (destination) {
                LaunchDestination.Authorization -> {
                    startAuthorizationActivity()
                }
                LaunchDestination.Main -> {
                    startMainActivity()
                }
            }
        })
    }

    private fun startAuthorizationActivity() {
        AuthorizationActivity.start(this)
        finish()
    }

    private fun startMainActivity() {
        MainActivity.start(this)
        finish()
    }
}