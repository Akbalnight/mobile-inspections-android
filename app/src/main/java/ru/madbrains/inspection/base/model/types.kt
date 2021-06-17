package ru.madbrains.inspection.base.model

import androidx.annotation.StringRes

sealed class TextData {
    class Str(val data: String) : TextData()
    class ResId(@StringRes val data: Int) : TextData()
}