package ru.madbrains.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetTokenResp(
    @field:Json(name = "access_token") val accessToken: String,
    @field:Json(name = "token_type") val tokenType: String,
    @field:Json(name = "refresh_token") val refreshToken: String,
    @field:Json(name = "expires_in") val expiresIn: Int,
    @field:Json(name = "scope") val scope: String,
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "permissions") val permissions: List<PermissionResp>,
    @field:Json(name = "code_challenge") val codeChallenge: String,
    @field:Json(name = "roles") val roles: String,
    @field:Json(name = "username") val username: String
)

@JsonClass(generateAdapter = true)
data class PermissionResp(
    @field:Json(name = "method") val method: String,
    @field:Json(name = "path") val path: String
)