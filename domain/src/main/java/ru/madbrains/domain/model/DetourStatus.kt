package ru.madbrains.domain.model

enum class DetourStatus(val id: String) {
    COMPLETED("a0299bf4-de93-40ab-9950-37392e3fd0a5"),
    NOT_COMPLETED("09b0ccca-3041-4cdd-b968-c621ae2ca758"),
    IN_PROGRESS("d13b8bbd-f08f-4e3f-93ac-7355c67333c1"),
    COMPLETED_AHEAD("7381f248-825b-4734-a45c-02603b0e8a25"),
    PAUSED("8bacd61b-d789-4cbd-8703-318510095047"),
    NEW("23782817-aa16-447a-ad65-bf3bf47ac3b7")
}