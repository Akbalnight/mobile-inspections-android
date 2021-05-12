package ru.madbrains.data.network

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class IAuthenticator constructor(
    private val context: Context
) : Authenticator {

    companion object {
        const val ACTION_UNAUTHORIZED = "notAuthorized"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val intent = Intent(ACTION_UNAUTHORIZED)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        return null
    }
}