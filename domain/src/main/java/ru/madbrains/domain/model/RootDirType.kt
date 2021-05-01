package ru.madbrains.domain.model

enum class RootDirType {
    Temp, Save
}
enum class AppDirType(val value:String) {
    Docs(DIR_DOCS), Defects(DIR_DEFECTS_MEDIA), Local(DIR_LOCAL)
}

private const val DIR_DEFECTS_MEDIA = "defects_media/"
private const val DIR_DOCS = "docs/"
private const val DIR_LOCAL = "local/"