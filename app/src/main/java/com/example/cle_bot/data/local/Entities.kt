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

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val controlNumber: String?,
    val email: String,
    val password: String // En una app real, esto debería estar hasheado
)

@Entity(tableName = "chat_messages")
data class ChatMessageLocalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int?,
    val text: String,
    val isBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "calificaciones")
data class CalificacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val periodo: String,
    val clave: String,
    val materia: String,
    val creditos: Int,
    val calificacion: String,
    val evaluacion: String
)
