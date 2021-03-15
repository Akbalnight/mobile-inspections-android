package ru.madbrains.data.network.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import ru.madbrains.data.network.ApiData
import ru.madbrains.data.network.response.GetTokenResp

interface AuthApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String = "${ApiData.apiUrl}/authorization_code",
        @Field("code") authCode: String,
        @Field("code_verifier") codeVerifier: String
    ): Single<GetTokenResp>


    @GET("/oauth/revokeToken")
    fun logout(@Query("token") token: String): Completable
}