package com.example.banka_3_mobile.user.di

import com.example.banka_3_mobile.user.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @Singleton
    fun provideCatsApi(@Named("UserRetrofit") retrofit: Retrofit): UserApi = retrofit.create()
}