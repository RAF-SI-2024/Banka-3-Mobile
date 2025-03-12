package com.example.banka_3_mobile.bank.di

import com.example.banka_3_mobile.bank.api.BankApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BankModule {
    @Singleton
    @Provides
    fun provideQuizApi(@Named("BankRetrofit") retrofit: Retrofit): BankApi {
        return retrofit.create(BankApi::class.java)
    }
}