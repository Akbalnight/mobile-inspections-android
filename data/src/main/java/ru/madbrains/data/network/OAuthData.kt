package ru.madbrains.data.network

import ru.madbrains.data.extensions.toBase64HashWith256

object OAuthData {

    var oauthUrl  = "http://10.5.31.117:8809"
    var clientId = "System-Service-Dias"
    var clientSecret = "24U7tcNLHRSvvjrr9sFEXGyMTpzk59mG"
    var authRedirectUrl = "http://10.5.31.117/authorization_code"

    fun getAuthorizeUrl(codeVerifier: String): String {
        return "$oauthUrl/oauth/authorize" +
                "?response_type=code" +
                "&client_id=$clientId" +
                "&redirect_uri=$authRedirectUrl" +
                "&scope=read&code_challenge=${codeVerifier.toBase64HashWith256()}" +
                "&code_challenge_method=s256"
    }
}