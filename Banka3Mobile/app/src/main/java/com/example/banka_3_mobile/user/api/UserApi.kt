package com.example.banka_3_mobile.user.api

import com.example.banka_3_mobile.user.model.CheckTokenDto
import com.example.banka_3_mobile.user.model.ClientGetResponse
import com.example.banka_3_mobile.user.model.LoginPostRequest
import com.example.banka_3_mobile.user.model.LoginPostResponse
import com.example.banka_3_mobile.verification.model.VerificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @POST("auth/login/client")
    suspend fun login(
        @Body request: LoginPostRequest
    ): LoginPostResponse

   @GET("admin/clients/me")
    suspend fun getUser(): ClientGetResponse

    @POST("auth/check-token")
    suspend fun checkToken(@Body checkTokenDto: CheckTokenDto): Response<Unit>

    @GET("verification/active-requests")
    suspend fun getActiveVerificationRequests(): List<VerificationRequest>

    @GET("verification/history")
    suspend fun getVerificationHistory(): List<VerificationRequest>

    @POST("verification/approve/{requestId}")
    suspend fun acceptVerificationRequest(@Path("requestId") requestId: Long): Response<Unit>

    @POST("verification/deny/{requestId}")
    suspend fun denyVerificationRequest(@Path("requestId") requestId: Long): Response<Unit>
}