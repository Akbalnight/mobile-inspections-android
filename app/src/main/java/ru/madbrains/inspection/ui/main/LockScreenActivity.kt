package ru.madbrains.inspection.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_lock_screen.*
import kotlinx.android.synthetic.main.toolbar_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.auth.AuthorizationActivity

class LockScreenActivity : BaseActivity(R.layout.activity_lock_screen) {

    private val lockScreenViewModel: LockScreenViewModel by viewModel()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LockScreenActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvTitle.text = strings[R.string.auth]
        btnLogin.setOnClickListener {
            lockScreenViewModel.login(tvUsername.text.toString(), tvPassword.text.toString())
        }
        btnLogout.setOnClickListener {
            lockScreenViewModel.logout()
        }
        tvUsername.addTextChangedListener {
            tvValidationError.isInvisible = true
        }
        tvPassword.addTextChangedListener {
            tvValidationError.isInvisible = true
        }
        lockScreenViewModel.navigateToMain.observe(this, EventObserver {
            MainActivity.start(this)
        })
        lockScreenViewModel.navigateToAuthorization.observe(this, EventObserver {
            AuthorizationActivity.start(this)
            this.finish()
        })
        lockScreenViewModel.showError.observe(this, EventObserver {
            tvValidationError.text = strings[it]
            tvValidationError.isInvisible = false
        })
        lockScreenViewModel.progressVisibility.observe(this, Observer {
            progressView.changeVisibility(it.first, it.second)
        })
    }
}