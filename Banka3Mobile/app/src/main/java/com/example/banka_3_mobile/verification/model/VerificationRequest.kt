package com.example.banka_3_mobile.verification.model

import kotlinx.serialization.Serializable

@Serializable
data class VerificationRequest(
    val id: Long,
    val userId: Long,
    val targetId: Long,
    val status: VerificationStatus,
    val verificationType: VerificationType,
    val expirationTime: String,
    val createdAt: String,
    val details: String
)
