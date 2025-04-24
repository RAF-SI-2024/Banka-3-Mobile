package com.example.banka_3_mobile.bank.payments.ips

import android.graphics.Bitmap
import com.example.banka_3_mobile.bank.payments.ips.model.IPSModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.set
import androidx.core.graphics.createBitmap

fun parseIPSQR(qrText: String): IPSModel {
    val map = qrText
        .split("|")
        .mapNotNull {
            val split = it.split(":", limit = 2)
            if (split.size == 2) split[0] to split[1] else null
        }.toMap()

    return IPSModel(
        accountNumber = map["R"],
        recipientName = map["N"]?.replace("\\r\\n", "\n"),
        amount = map["I"],
        payer = map["P"]?.replace("\\r\\n", "\n"),
        paymentCode = map["SF"],
        purpose = map["S"],
        referenceNumber = map["RO"]
    )
}
fun parseIPSQRRaw(qrText: String): Map<String, String> {
    return qrText
        .split("|")
        .mapNotNull {
            val split = it.split(":", limit = 2)
            if (split.size == 2) split[0] to split[1] else null
        }.toMap()
}


fun generateIPSQRString(
    accountNumber: String,
    recipientName: String,
    amount: Int?,
    paymentCode: String,
    purpose: String,
    referenceNumber: String? = null,
    payerInfo: String? = null,
    currencyCode: String = "RSD"
): String {
    return buildList {
        add("K:PR")
        add("V:01")
        add("C:1")
        add("R:$accountNumber")
        add("N:$recipientName")
        amount?.let {
            add("I:$currencyCode%.2f".format(it.toDouble()).replace('.', ','))
        }
        payerInfo?.let { add("P:$it") }
        add("SF:$paymentCode")
        add("S:$purpose")
        referenceNumber?.let { add("RO:$it") }
    }.joinToString("|")
}

fun isValidIPSQR(map: Map<String, String>): Boolean {
    return map.containsKey("R") && map.containsKey("N") &&
            map.containsKey("SF") && map.containsKey("S")
}

fun generateQRBitmap(text: String, size: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
    val bmp = createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return bmp
}
