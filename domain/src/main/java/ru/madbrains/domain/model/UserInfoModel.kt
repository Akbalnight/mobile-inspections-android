package ru.madbrains.domain.model

data class UserInfoModel(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Int,
    val scope: String,
    val userId: String,
    val permissions: List<PermissionModel>,
    val codeChallenge: String,
    val roles: String,
    val username: String
)

data class PermissionModel(
    val method: String,
    val path: String
)