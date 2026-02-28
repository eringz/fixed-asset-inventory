package com.example.fixedassetinventory.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fixedassetinventory.data.entity.User

@Dao
interface UserDao {
//    @Insert
//    suspend fun registerUser(user: user)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun login(username: String): User?
}