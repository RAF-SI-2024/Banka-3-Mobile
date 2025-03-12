package com.example.banka_3_mobile.user.account.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

class AccountDataStore @Inject constructor(
    private val dataStore: DataStore<AccountData>
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val data = dataStore.data
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking { dataStore.data.first() },
        )

    val dataFlow = dataStore.data

    suspend fun updateProfileData(
        data: AccountData
    ) {
        try {
            Log.d("raf", "updatam datu")
            dataStore.updateData { data }
            Log.d("raf", "gotov update")
        } catch (exception: IOException) {
            Log.e("ProfileDataStore", "Error updating profile data", exception)
        }
    }

}