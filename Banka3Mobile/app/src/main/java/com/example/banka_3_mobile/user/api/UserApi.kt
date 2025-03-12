package com.example.banka_3_mobile.user.api

import com.example.banka_3_mobile.user.model.CheckTokenDto
import com.example.banka_3_mobile.user.model.ClientGetResponse
import com.example.banka_3_mobile.user.model.LoginPostRequest
import com.example.banka_3_mobile.user.model.LoginPostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @POST("auth/login/client")
    suspend fun login(
        @Body request: LoginPostRequest
    ): LoginPostResponse

   @GET("admin/clients/me")
    suspend fun getUser(): ClientGetResponse

    @POST("auth/check-token")
    suspend fun checkToken(@Body checkTokenDto: CheckTokenDto): Response<Unit>
}