package ru.madbrains.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.domain.model.UserInfoModel

interface AuthRepository {
    fun getToken(authCode: String, codeVerifier: String): Single<UserInfoModel>

    fun refreshToken(token: String): Single<UserInfoModel>

    fun logout(accessToken: String): Completable
}