package ru.madbrains.domain.model

import java.io.Serializable
import java.util.*

data class SyncInfo(
    val getDate: Date? = null,
    val sendDate: Date? = null
) : Serializable