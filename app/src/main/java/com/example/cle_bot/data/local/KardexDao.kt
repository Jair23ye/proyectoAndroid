package com.example.cle_bot.data.local

import androidx.room.*

@Dao
interface KardexDao {
    @Query("SELECT * FROM calificaciones WHERE userId = :userId")
    suspend fun getKardex(userId: Int): List<CalificacionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(calificaciones: List<CalificacionEntity>)

    @Query("DELETE FROM calificaciones WHERE userId = :userId")
    suspend fun deleteForUser(userId: Int)
}
