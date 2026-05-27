package com.example.cle_bot.data

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class CleBotRepository(
    private val api: CleBotApi = ApiClient.api
) {
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return safeCall {
            api.login(LoginRequest(email = email.trim(), password = password))
        }
    }

    suspend fun register(
        name: String,
        controlNumber: String,
        email: String,
        password: String
    ): ApiResult<RegisterResponse> {
        return safeCall {
            api.register(
                RegisterRequest(
                    name = name.trim(),
                    controlNumber = controlNumber.trim().ifBlank { null },
                    email = email.trim(),
                    password = password
                )
            )
        }
    }

    suspend fun forgotPassword(email: String): ApiResult<ForgotPasswordResponse> {
        return safeCall {
            api.forgotPassword(ForgotPasswordRequest(email = email.trim()))
        }
    }

    suspend fun resetPassword(token: String, newPassword: String): ApiResult<ResetPasswordResponse> {
        return safeCall {
            api.resetPassword(ResetPasswordRequest(token = token.trim(), newPassword = newPassword))
        }
    }

    suspend fun createSupportTicket(
        userId: Int?,
        name: String,
        email: String,
        category: String,
        description: String
    ): ApiResult<SupportTicketResponse> {
        return safeCall {
            api.createSupportTicket(
                SupportTicketRequest(
                    userId = userId,
                    name = name.trim(),
                    email = email.trim(),
                    category = category.trim(),
                    description = description.trim()
                )
            )
        }
    }

    suspend fun sendChatMessage(userId: Int?, message: String): ApiResult<ChatMessageResponse> {
        return safeCall {
            api.sendChatMessage(ChatMessageRequest(userId = userId, message = message.trim()))
        }
    }

    suspend fun getKardex(userId: Int): ApiResult<KardexResponse> {
        return safeCall {
            api.getKardex(userId)
        }
    }

    private suspend fun <T : ApiResponse> safeCall(call: suspend () -> T): ApiResult<T> {
        return try {
            val response = call()
            if (response.success) {
                ApiResult.Success(response)
            } else {
                ApiResult.Error(response.message ?: "La operacion no se pudo completar.")
            }
        } catch (exception: HttpException) {
            ApiResult.Error(readErrorMessage(exception))
        } catch (exception: IOException) {
            ApiResult.Error("No se pudo conectar con XAMPP. Verifica que Apache y MySQL esten encendidos.")
        } catch (exception: Exception) {
            ApiResult.Error(exception.message ?: "Ocurrio un error inesperado.")
        }
    }

    private fun readErrorMessage(exception: HttpException): String {
        val body = exception.response()?.errorBody()?.string().orEmpty()
        return runCatching {
            Gson().fromJson(body, ErrorResponse::class.java).message
        }.getOrNull()?.takeIf { it.isNotBlank() }
            ?: "El servidor respondio con error ${exception.code()}."
    }
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

private data class ErrorResponse(
    val success: Boolean = false,
    val message: String = ""
)
