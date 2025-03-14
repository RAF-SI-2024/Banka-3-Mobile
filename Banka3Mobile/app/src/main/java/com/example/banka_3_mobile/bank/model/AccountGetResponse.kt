package com.example.banka_3_mobile.bank.model


@kotlinx.serialization.Serializable
data class AccountGetResponse(
    val accountNumber: String,
    val clientId: Long,
    val companyId: Long? = null,
    val createdByEmployeeId: Long? = null,
    val creationDate: String,
    val expirationDate: String,
    val currencyCode: String,
    val status: AccountStatus,
    val balance: Float,
    val availableBalance: Float,
    val dailyLimit: Float,
    val monthlyLimit: Float,
    val dailySpending: Float,
    val monthlySpending: Float,
    val owner: ClientDto,
    // "lični" ili "poslovni"
    val ownershipType: String,
    // "tekući" ili "devizni"
    val accountCategory: String
)

@kotlinx.serialization.Serializable
data class ClientDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
)


@kotlinx.serialization.Serializable
enum class AccountStatus {
    ACTIVE,
    INACTIVE,
}