package com.example.fixedassetinventory.utils

import java.security.MessageDigest
object SecurityUtils {
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }
}