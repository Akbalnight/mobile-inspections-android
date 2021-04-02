package ru.madbrains.domain.model

enum class DefectStatus(val id: String) {
    NEW("1864073a-bf8d-4df2-b02d-8e5afa63c4d0"),
    IN_PROGRESS("879f0adf-0d96-449e-bcee-800f81c4e58d"),
    EXPIRED("df7d1216-6eb7-4a00-93a4-940047e8b9c0"),
    ELIMINATED("16f09a44-11fc-4f82-b7b5-1eb2e812d8fa"),
    CONFIRMED("83b4bbf8-e1da-43d4-8e0d-a973a136eeaa"),
    RESUMED("086d775e-f41b-4af6-86c8-31f340344f47")
}