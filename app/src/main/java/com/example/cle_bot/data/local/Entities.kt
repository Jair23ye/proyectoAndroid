package com.example.cle_bot.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tramites")
data class TramiteEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String
)

@Entity(tableName = "requisitos")
data class RequisitoEntity(
    @PrimaryKey val id: Int,
    val tramiteId: Int,
    val description: String,
    val isMandatory: Boolean
)

@Entity(tableName = "pasos")
data class PasoEntity(
    @PrimaryKey val id: Int,
    val tramiteId: Int,
    val stepNumber: Int,
    val title: String,
    val description: String
)

@Entity(tableName = "progreso_usuario", primaryKeys = ["userId", "tramiteId", "pasoId"])
data class ProgresoEntity(
    val userId: Int,
    val tramiteId: Int,
    val pasoId: Int,
    val isCompleted: Boolean,
    val isSynced: Boolean = false
)
