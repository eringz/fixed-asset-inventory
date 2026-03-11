package com.example.fixedassetinventory

import android.app.Application
import com.example.fixedassetinventory.data.AppDatabase
import com.example.fixedassetinventory.data.AssetRepositoryImpl
import com.example.fixedassetinventory.utils.ExportService
import com.example.fixedassetinventory.utils.ImportService

class BaseApplication: Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val assetRepository by lazy {
        AssetRepositoryImpl(database.assetDao())
    }

    val exportService by lazy {
        ExportService(this)
    }

    val importService by lazy {
        ImportService(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}