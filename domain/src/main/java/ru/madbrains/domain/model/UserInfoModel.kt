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
    val roles: List<UserRole>,
    val username: String
) {
    val isAdmin: Boolean = roles.contains(UserRole.ROLE_ADMIN)
}

data class PermissionModel(
    val method: String,
    val path: String
)

enum class UserRole{
    ROLE_MOBILE_APP,
    ROLE_README,
    ROLE_ADMIN,
    ROLE_PORTAL
}