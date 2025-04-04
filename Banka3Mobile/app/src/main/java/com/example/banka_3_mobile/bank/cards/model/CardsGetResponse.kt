package com.example.banka_3_mobile.bank.cards.model

import com.example.banka_3_mobile.bank.model.ClientDto
import kotlinx.serialization.Serializable

@Serializable
data class CardsGetResponse(
    val id: Long? = null,
    val cardNumber: String,
    val cvv: String,
    val type: String? = null,
    val name: String? = null,
    val creationDate: String,
    val expirationDate: String,
    val accountNumber: String,
    val status: CardStatus,
    val cardLimit: Float,
    val owner: ClientDto
)

@Serializable
enum class CardStatus {
    ACTIVE,
    BLOCKED,
    DEACTIVATED
}
