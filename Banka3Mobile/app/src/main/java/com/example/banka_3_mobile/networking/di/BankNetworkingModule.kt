package com.example.banka_3_mobile.networking.di

import android.util.Log
import com.example.banka_3_mobile.networking.serialization.AppJson
import com.example.banka_3_mobile.user.account.datastore.AccountDataStore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BankNetworkingModule {

    @Singleton
    @Provides
    @Named("BankOkHttp")
    fun provideOkHttpClient(
        accountDataStore: AccountDataStore
    ) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                    chain ->
                val token = runBlocking {
                    accountDataStore.data.first().token
                }
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
    }

    @Singleton
    @Provides
    @Named("BankRetrofit")
    fun provideRetrofitClient(
        @Named("BankOkHttp") okHttpClient: OkHttpClient,
    ) : Retrofit {
        Log.d("raf", "Provide retrofit for bank")
        return Retrofit.Builder()
           // .baseUrl("http://localhost:8082/api/")
            .baseUrl("http://10.0.2.2:8082/api/")
            .client(okHttpClient)
            .addConverterFactory(AppJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}