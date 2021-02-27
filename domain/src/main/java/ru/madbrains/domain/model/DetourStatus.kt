package ru.madbrains.domain.model

enum class DetourStatus(val id: String) {
    PENDING("da1aa97a-755d-41f0-ac60-3d61d7f8d426"),
    COMPLETED("a0299bf4-de93-40ab-9950-37392e3fd0a5"),
    NOT_COMPLETED("09b0ccca-3041-4cdd-b968-c621ae2ca758"),
    IN_PROGRESS("d13b8bbd-f08f-4e3f-93ac-7355c67333c1"),
    COMPLETED_AHEAD("7381f248-825b-4734-a45c-02603b0e8a25")
}