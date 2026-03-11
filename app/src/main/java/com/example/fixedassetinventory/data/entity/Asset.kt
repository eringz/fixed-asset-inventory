package com.example.fixedassetinventory.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset (
    @PrimaryKey
    @ColumnInfo(name = "asset_number")
    val assetNumber: String,

    val description: String,
    val location: String,
    val remarks: String,
    val validate: String = "Not Found",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)