package com.example.banka_3_mobile.user.jwt

import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun decodeJwtPayload(token: String): Map<String, String>? {
    try {
        val parts = token.split(".")
        if (parts.size != 3) return null
        val payloadEncoded = parts[1]
        val decodedBytes = Base64.decode(payloadEncoded, Base64.URL_SAFE or Base64.NO_WRAP)
        val decodedString = String(decodedBytes)
        val jsonElement = Json.parseToJsonElement(decodedString)
        val jsonObject = jsonElement.jsonObject
        return jsonObject.mapValues { it.value.jsonPrimitive.content }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


fun isClientToken(token: String): Boolean {
    val payload = decodeJwtPayload(token) ?: return false
    val role = payload["role"] ?: return false
    return role == "CLIENT"
}
