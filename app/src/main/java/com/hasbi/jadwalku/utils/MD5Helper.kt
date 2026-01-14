package com.hasbi.jadwalku.utils

import java.security.MessageDigest

object MD5Helper {

    /**
     * Enkripsi string ke MD5 hash
     * Digunakan untuk password sebelum dikirim ke server
     */
    fun md5(input: String): String {
        try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())

            // Convert byte array ke hex string
            val hexString = StringBuilder()
            for (byte in messageDigest) {
                val hex = Integer.toHexString(0xFF and byte.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }

            return hexString.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * Verifikasi password
     * Compare input password dengan hash yang disimpan
     */
    fun verify(input: String, hash: String): Boolean {
        val inputHash = md5(input)
        return inputHash.equals(hash, ignoreCase = true)
    }
}