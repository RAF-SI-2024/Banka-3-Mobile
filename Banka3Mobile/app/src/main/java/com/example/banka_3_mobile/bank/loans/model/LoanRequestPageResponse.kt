package com.example.banka_3_mobile.bank.loans.model

import kotlinx.serialization.Serializable

@Serializable
data class LoanRequestPageResponse(
    val content: List<LoanRequestGetResponse>
    // val pageable: Pageable,
    // val last: Boolean,
    // val totalPages: Int,
    // val totalElements: Int,
    // val first: Boolean,
    // val size: Int,
    // val number: Int,
    // val sort: Sort,
    // val numberOfElements: Int,
    // val empty: Boolean
)
