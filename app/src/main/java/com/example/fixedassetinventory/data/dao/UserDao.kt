package com.example.fixedassetinventory.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fixedassetinventory.data.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun register(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :pass LIMIT 1")
    suspend fun login(username: String, pass: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun userExists(username: String): Boolean
}