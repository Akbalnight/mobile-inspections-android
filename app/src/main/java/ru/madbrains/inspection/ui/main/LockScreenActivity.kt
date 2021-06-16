package ru.madbrains.inspection.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import kotlinx.android.synthetic.main.toolbar_empty.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity
import ru.madbrains.inspection.extensions.strings

class LockScreenActivity : BaseActivity(R.layout.activity_lock_screen) {

    private lateinit var navController: NavController

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LockScreenActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val lockScreenViewModel: LockScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupMenu()
    }

    private fun setupMenu() {
        tvTitle.text = strings[R.string.auth]
    }

    private fun startAuthActivity() {
        MainActivity.start(this)
        this.finish()
    }
}