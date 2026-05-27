package com.example.cle_bot.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TramiteDao {
    @Query("SELECT * FROM tramites")
    suspend fun getAllTramites(): List<TramiteEntity>

    @Query("SELECT * FROM tramites WHERE title LIKE '%' || :query || '%'")
    suspend fun searchTramites(query: String): List<TramiteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTramites(tramites: List<TramiteEntity>)

    @Query("SELECT * FROM tramites WHERE id = :id")
    suspend fun getTramiteById(id: Int): TramiteEntity?

    @Query("SELECT * FROM requisitos WHERE tramiteId = :tramiteId")
    suspend fun getRequisitosByTramiteId(tramiteId: Int): List<RequisitoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequisitos(requisitos: List<RequisitoEntity>)

    @Query("SELECT * FROM pasos WHERE tramiteId = :tramiteId ORDER BY stepNumber ASC")
    suspend fun getPasosByTramiteId(tramiteId: Int): List<PasoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasos(pasos: List<PasoEntity>)
    
    @Query("SELECT * FROM progreso_usuario WHERE userId = :userId AND tramiteId = :tramiteId")
    suspend fun getProgreso(userId: Int, tramiteId: Int): List<ProgresoEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgreso(progreso: ProgresoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgresos(progresos: List<ProgresoEntity>)
    
    @Query("SELECT * FROM progreso_usuario WHERE isSynced = 0")
    suspend fun getUnsyncedProgress(): List<ProgresoEntity>
    
    @Query("UPDATE progreso_usuario SET isSynced = 1 WHERE userId = :userId AND tramiteId = :tramiteId AND pasoId = :pasoId")
    suspend fun markAsSynced(userId: Int, tramiteId: Int, pasoId: Int)
}
