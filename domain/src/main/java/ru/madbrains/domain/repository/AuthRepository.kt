package ru.madbrains.domain.repository

import io.reactivex.Single
import ru.madbrains.domain.model.UserInfoModel

interface AuthRepository {
    fun getToken(authCode: String, codeVerifier: String): Single<UserInfoModel>
}