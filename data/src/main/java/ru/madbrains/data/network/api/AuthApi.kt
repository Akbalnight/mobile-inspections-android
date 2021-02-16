package ru.madbrains.data.network.api

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.madbrains.data.network.response.GetTokenResp

interface AuthApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String = "http://10.5.31.117/authorization_code",
        @Field("code") authCode: String,
        @Field("code_verifier") codeVerifier: String
    ): Single<GetTokenResp>
}