package com.example.cle_bot.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CleBotApi {
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/forgot-password.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @POST("auth/reset-password.php")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    @POST("support/create-ticket.php")
    suspend fun createSupportTicket(@Body request: SupportTicketRequest): SupportTicketResponse

    @POST("chat/message.php")
    suspend fun sendChatMessage(@Body request: ChatMessageRequest): ChatMessageResponse

    @GET("tramites/list.php")
    suspend fun getTramites(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null
    ): TramiteListResponse

    @GET("tramites/detail.php")
    suspend fun getTramiteDetail(
        @Query("id") id: Int
    ): TramiteDetailResponse

    @GET("tramites/progress.php")
    suspend fun getProgreso(
        @Query("user_id") userId: Int,
        @Query("tramite_id") tramiteId: Int
    ): ProgresoListResponse

    @POST("tramites/progress.php")
    suspend fun saveProgreso(@Body request: SaveProgresoRequest): ApiResponse

    @GET("tramites/kardex.php")
    suspend fun getKardex(
        @Query("user_id") userId: Int
    ): KardexResponse
}
