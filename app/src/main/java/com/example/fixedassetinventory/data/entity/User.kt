package com.example.fixedassetinventory.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val username: String,
    val passwordHash: String
)
