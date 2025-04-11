package com.example.banka_3_mobile.verification.mapper

import com.example.banka_3_mobile.verification.model.ChangeAccountLimitVerificationDetails
import com.example.banka_3_mobile.verification.model.NewCardVerificationDetails
import com.example.banka_3_mobile.verification.model.PaymentAndTransferVerificationDetails
import kotlinx.serialization.json.Json

fun mapPaymentOrTransferDetails(json: String): PaymentAndTransferVerificationDetails {

    val raw = Json.decodeFromString<PaymentAndTransferVerificationDetails>(json)

    return PaymentAndTransferVerificationDetails(
        fromAccountNumber = raw.fromAccountNumber.toLong(),
        toAccountNumber = raw.toAccountNumber.toLong(),
        amount = raw.amount.toFloat()
    )
}

fun mapChangeLimitDetails(json: String): ChangeAccountLimitVerificationDetails {

    val raw = Json.decodeFromString<ChangeAccountLimitVerificationDetails>(json)

    return ChangeAccountLimitVerificationDetails(
        accountNumber = raw.accountNumber,
        oldLimit = raw.oldLimit.toFloat(),
        newLimit = raw.newLimit.toFloat()
    )
}

fun mapNewCreditCardVerification(json: String): NewCardVerificationDetails {
    val raw = Json.decodeFromString<NewCardVerificationDetails>(json)

    return NewCardVerificationDetails(
        name = raw.name,
        issuer = raw.issuer,
        type = raw.type,
        accountNumber = raw.accountNumber
    )
}