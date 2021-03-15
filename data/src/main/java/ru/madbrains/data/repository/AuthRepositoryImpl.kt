package ru.madbrains.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.network.OAuthData
import ru.madbrains.data.network.mappers.mapGetTokenResp
import ru.madbrains.data.network.request.LogoutReq
import ru.madbrains.domain.model.UserInfoModel
import ru.madbrains.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {
    override fun getToken(authCode: String, codeVerifier: String): Single<UserInfoModel> {
        return OAuthData.authApi.getToken(
            authCode = authCode,
            codeVerifier = codeVerifier
        ).map { resp ->
            mapGetTokenResp(resp)
        }
    }

    override fun logout(accessToken: String): Completable {
        val request = LogoutReq(
            accessToken = accessToken
        )
        return authApi.logout(request)
    }
}