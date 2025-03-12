package com.example.banka_3_mobile.user.repository

import com.example.banka_3_mobile.user.api.UserApi
import com.example.banka_3_mobile.user.model.CheckTokenDto
import com.example.banka_3_mobile.user.model.ClientGetResponse
import com.example.banka_3_mobile.user.model.LoginPostRequest
import com.example.banka_3_mobile.user.model.LoginPostResponse
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi
) {

    suspend fun login(email: String, password: String): LoginPostResponse {
        return userApi.login(LoginPostRequest(email, password))
    }

    suspend fun getUser(): ClientGetResponse {
        return userApi.getUser()
    }

    suspend fun checkToken(token: String): Boolean {
        val response = userApi.checkToken(CheckTokenDto(token))
        return response.isSuccessful
    }
}