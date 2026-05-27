package com.example.cle_bot.data

import com.google.gson.annotations.SerializedName

interface ApiResponse {
    val success: Boolean
    val message: String?
}

data class UserDto(
    val id: Int,
    val name: String,
    val controlNumber: String?,
    val email: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    override val success: Boolean,
    override val message: String?,
    val user: UserDto?
) : ApiResponse

data class RegisterRequest(
    val name: String,
    val controlNumber: String?,
    val email: String,
    val password: String
)

data class RegisterResponse(
    override val success: Boolean,
    override val message: String?,
    val userId: Int?
) : ApiResponse

data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    override val success: Boolean,
    override val message: String?,
    val resetToken: String?
) : ApiResponse

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

data class ResetPasswordResponse(
    override val success: Boolean,
    override val message: String?
) : ApiResponse

data class SupportTicketRequest(
    val userId: Int?,
    val name: String,
    val email: String,
    val category: String,
    val description: String
)

data class SupportTicketResponse(
    override val success: Boolean,
    override val message: String?,
    val ticketId: Int?
) : ApiResponse

data class ChatMessageRequest(
    val userId: Int?,
    val message: String
)

data class ChatMessageResponse(
    override val success: Boolean,
    override val message: String?,
    @SerializedName("botReply")
    val botReply: String?
) : ApiResponse

data class TramiteDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val requisitos: List<RequisitoDto>? = null,
    val pasos: List<PasoDto>? = null
)

data class RequisitoDto(
    val id: Int,
    val tramite_id: Int,
    val description: String,
    val is_mandatory: Int
)

data class PasoDto(
    val id: Int,
    val tramite_id: Int,
    val step_number: Int,
    val title: String,
    val description: String
)

data class TramiteListResponse(
    override val success: Boolean,
    override val message: String?,
    val data: List<TramiteDto>?
) : ApiResponse

data class TramiteDetailResponse(
    override val success: Boolean,
    override val message: String?,
    val data: TramiteDto?
) : ApiResponse

data class ProgresoDto(
    val paso_id: Int,
    val is_completed: Int
)

data class ProgresoListResponse(
    override val success: Boolean,
    override val message: String?,
    val data: List<ProgresoDto>?
) : ApiResponse

data class SaveProgresoRequest(
    val user_id: Int,
    val tramite_id: Int,
    val paso_id: Int,
    val is_completed: Int
)

data class CalificacionDto(
    val periodo: String,
    val clave: String,
    val materia: String,
    val creditos: Int,
    val calificacion: String,
    val evaluacion: String,
    val observaciones: String?
)

data class KardexResponse(
    override val success: Boolean,
    override val message: String?,
    val data: List<CalificacionDto>?,
    val promedioGeneral: Double? = null,
    val creditosTotales: Int? = null,
    val creditosAprobados: Int? = null
) : ApiResponse
