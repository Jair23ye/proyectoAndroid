package com.example.cle_bot.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun register(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?
}
