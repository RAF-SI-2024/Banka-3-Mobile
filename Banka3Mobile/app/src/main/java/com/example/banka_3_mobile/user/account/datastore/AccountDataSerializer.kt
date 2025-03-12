package com.example.banka_3_mobile.user.account.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.banka_3_mobile.networking.serialization.AppJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class AccountDataSerializer : Serializer<AccountData> {

    override val defaultValue: AccountData = AccountData.EMPTY

    override suspend fun readFrom(input: InputStream): AccountData {
        val text = String(input.readBytes(), charset = StandardCharsets.UTF_8)
        return try {
            AppJson.decodeFromString<AccountData>(text)
        } catch (error: SerializationException) {
            throw CorruptionException(message = "Unable to deserialize file.", cause = error)
        }
    }

    override suspend fun writeTo(t: AccountData, output: OutputStream) {
        val text = AppJson.encodeToString(t)
        withContext(Dispatchers.IO) {
            output.write(text.toByteArray(charset = StandardCharsets.UTF_8))
        }
    }
}