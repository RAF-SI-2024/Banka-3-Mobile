package com.example.banka_3_mobile.user.account.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.banka_3_mobile.user.account.datastore.AccountData
import com.example.banka_3_mobile.user.account.datastore.AccountDataSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {

    @Singleton
    @Provides
    fun provideProfileDataStore(
        @ApplicationContext context: Context
    ): DataStore<AccountData> {
        return DataStoreFactory.create(
            produceFile = { context.dataStoreFile(fileName = "profile.json") },
            serializer = AccountDataSerializer(),
        )
    }
}