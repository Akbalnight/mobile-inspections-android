package ru.madbrains.domain.interactor

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.UserInfoModel
import ru.madbrains.domain.repository.AuthRepository

class AuthInteractor(
    private val authRepository: AuthRepository
) {
    fun getToken(authCode: String, codeVerifier: String): Single<UserInfoModel> {
        return authRepository.getToken(authCode, codeVerifier)
            .subscribeOn(Schedulers.io())
    }

    fun logout(accessToken: String): Completable {
        return authRepository.logout(accessToken)
            .subscribeOn(Schedulers.io())
    }
}