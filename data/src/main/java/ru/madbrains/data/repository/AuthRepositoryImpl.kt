package ru.madbrains.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.network.OAuthData
import ru.madbrains.data.network.mappers.mapGetTokenResp
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.model.UserInfoModel
import ru.madbrains.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val preferenceStorage: PreferenceStorage
) : AuthRepository {
    override fun getToken(authCode: String, codeVerifier: String): Single<UserInfoModel> {
        return OAuthData.authApi.getToken(
            authCode = authCode,
            codeVerifier = codeVerifier,
            redirectUri = "${preferenceStorage.apiUrl}/authorization_code"
        ).map { resp ->
            mapGetTokenResp(resp)
        }
    }

    override fun refreshToken(token: String): Single<UserInfoModel> {
        return OAuthData.authApi.refreshToken(
            token = token
        ).map { resp ->
            mapGetTokenResp(resp)
        }
    }

    override fun logout(accessToken: String): Completable {
        return OAuthData.authApi.logout(accessToken)
    }
}