package com.example.banka_3_mobile.verification.model

import kotlinx.serialization.Serializable

@Serializable
enum class VerificationStatus {
    PENDING,
    APPROVED,
    DENIED,
    EXPIRED,
}