package com.example.fixedassetinventory.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatMillis(millis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}