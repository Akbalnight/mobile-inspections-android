package ru.madbrains.data.repository

import io.reactivex.Single
import ru.madbrains.data.network.api.AuthApi
import ru.madbrains.data.network.mappers.mapGetTokenResp
import ru.madbrains.domain.model.UserInfoModel
import ru.madbrains.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {
    override fun getToken(authCode: String, codeVerifier: String): Single<UserInfoModel> {
        return authApi.getToken(
            authCode = authCode,
            codeVerifier = codeVerifier
        ).map { resp ->
            mapGetTokenResp(resp)
        }
    }
}