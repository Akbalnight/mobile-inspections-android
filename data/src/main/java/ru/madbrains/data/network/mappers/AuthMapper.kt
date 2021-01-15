package ru.madbrains.data.network.mappers

import ru.madbrains.data.network.response.GetTokenResp
import ru.madbrains.data.network.response.PermissionResp
import ru.madbrains.domain.model.PermissionModel
import ru.madbrains.domain.model.UserInfoModel

fun mapGetTokenResp(resp: GetTokenResp): UserInfoModel {
    return with(resp) {
        UserInfoModel(
            accessToken = accessToken,
            tokenType = tokenType,
            refreshToken = refreshToken,
            expiresIn = expiresIn,
            scope = scope,
            userId = userId,
            permissions = permissions.map { mapPermissionResp(it) },
            codeChallenge = codeChallenge,
            roles = roles,
            username = username
        )
    }
}

fun mapPermissionResp(resp: PermissionResp): PermissionModel {
    return with(resp) {
        PermissionModel(
            method = method,
            path = path
        )
    }
}