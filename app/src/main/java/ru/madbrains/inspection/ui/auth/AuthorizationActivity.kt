package ru.madbrains.inspection.ui.auth

import android.content.Context
import android.content.Intent
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity

class AuthorizationActivity : BaseActivity(R.layout.activity_authorization) {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AuthorizationActivity::class.java)
            context.startActivity(intent)
        }
    }
}