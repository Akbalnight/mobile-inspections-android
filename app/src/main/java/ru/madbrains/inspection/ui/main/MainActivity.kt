package ru.madbrains.inspection.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_navigation_menu.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity
import ru.madbrains.inspection.base.EventObserver

class MainActivity : BaseActivity(R.layout.activity_main) {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupMenu()

        mainViewModel.navigateToMenu.observe(this, EventObserver {
            mainDrawer.openDrawer(GravityCompat.START)
        })
    }

    private fun setupMenu() {
        tvUsername.text = mainViewModel.username
    }
}