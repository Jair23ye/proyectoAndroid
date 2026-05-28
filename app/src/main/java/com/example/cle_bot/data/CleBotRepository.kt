package com.example.cle_bot.data

import android.util.Log
import com.example.cle_bot.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

class CleBotRepository(
    private val userDao: UserDao,
    private val chatDao: ChatDao,
    private val kardexDao: KardexDao,
    private val tramiteDao: TramiteDao
) {
    // Eliminado baseUrl de XAMPP

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> = withContext(Dispatchers.IO) {
        val localUser = userDao.login(email.trim(), password)
        if (localUser != null) {
            ApiResult.Success(LoginResponse(
                success = true, 
                message = "Sesión iniciada localmente", 
                user = UserDto(localUser.id, localUser.name, localUser.controlNumber, localUser.email, "estudiante")
            ))
        } else {
            ApiResult.Error("Usuario no encontrado en el control interno. ¿Ya te registraste?")
        }
    }

    suspend fun register(name: String, controlNumber: String, email: String, password: String): ApiResult<RegisterResponse> = withContext(Dispatchers.IO) {
        try {
            userDao.register(UserEntity(
                name = name.trim(), 
                controlNumber = controlNumber.trim(), 
                email = email.trim(), 
                password = password
            ))
            ApiResult.Success(RegisterResponse(true, "Registrado en control interno", 0))
        } catch (e: Exception) {
            ApiResult.Error("Error al registrar: ${e.localizedMessage}")
        }
    }

    suspend fun sendChatMessage(userId: Int?, message: String): ApiResult<ChatMessageResponse> = withContext(Dispatchers.IO) {
        chatDao.insertMessage(ChatMessageLocalEntity(userId = userId, text = message, isBot = false))
        
        try {
            val user = userDao.getUserById(userId ?: 1)
            val numControl = user?.controlNumber ?: "21270156"
            val contextData = buildAIContext(userId ?: 1)

            val client = OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            
            val mediaType = "application/json".toMediaType()
            val apiKey = "sk-39f1a69d0ce84cdea5164c1932d45035"
            
            val json = """
                {
                    "model": "deepseek-chat",
                    "messages": [
                        {
                            "role": "system", 
                            "content": "Eres CLEbot, el asistente experto del Instituto Tecnológico de Tuxtla Gutiérrez (ITTG). REGLAS DE ORO: 1. NUNCA INVENTES NOMBRES, FECHAS O DATOS QUE NO CONOZCAS. 2. Si te preguntan algo sobre la institución que NO está en el contexto (como quién es el director), usa tu conocimiento general verídico o indica que debe consultarse en la página oficial. 3. Para datos del alumno y trámites específicos, usa el 'CONTEXTO' proporcionado. 4. Sé honesto sobre tus fuentes: si algo no está en el contexto, no afirmes que lo sacaste de ahí. 5. El director actual (2024-2026) es el M.C. José Manuel Rosado Pérez. 6. El portal oficial es https://estudiantes.tuxtla.tecnm.mx/."
                        },
                        {
                            "role": "user", 
                            "content": "CONTEXTO DE VERDAD ABSOLUTA (MAYO 2026):\n\n${contextData.replace("\"", "\\\"").replace("\n", "\\n")}\n\nPREGUNTA DEL ALUMNO: ${message.replace("\"", "\\\"").replace("\n", "\\n")}"
                        }
                    ],
                    "max_tokens": 1500,
                    "temperature": 0.1
                }
            """.trimIndent()

            val request = Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .post(json.toRequestBody(mediaType))
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            
            if (response.isSuccessful && body != null) {
                val botReply = extractContent(body)
                chatDao.insertMessage(ChatMessageLocalEntity(userId = userId, text = botReply, isBot = true))
                ApiResult.Success(ChatMessageResponse(true, "AI", botReply))
            } else {
                Log.e("CleBotRepository", "Error API AI: ${response.code} - ${response.message}")
                throw Exception("Error API AI: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("CleBotRepository", "Exception in sendChatMessage", e)
            val errorMsg = when (e) {
                is java.net.UnknownHostException -> "Sin conexión a internet"
                is java.net.SocketTimeoutException -> "La conexión ha expirado"
                else -> "Error de servidor (${e.localizedMessage})"
            }
            val reply = "Control interno: Recibí tu mensaje, pero hubo un problema técnico: $errorMsg. Mi IA se encuentra temporalmente fuera de línea."
            chatDao.insertMessage(ChatMessageLocalEntity(userId = userId, text = reply, isBot = true))
            ApiResult.Success(ChatMessageResponse(true, "Offline", reply))
        }
    }

    private suspend fun buildAIContext(userId: Int?): String {
        val user = userDao.getUserById(userId ?: 1)
        val nombreAlumno = user?.name ?: "JULIO ALEJANDRO MEDINA CERVANTES"
        val numControl = user?.controlNumber ?: "21270156"

        val localKardex = kardexDao.getKardex(userId ?: 1)
        
        // Formatear el Kardex por periodos para que el bot no se pierda
        val kardexByPeriod = localKardex.groupBy { it.periodo }
        val kardexBuilder = StringBuilder()
        
        kardexByPeriod.forEach { (periodo, materias) ->
            kardexBuilder.append("\nPERIODO: $periodo\n")
            materias.forEach { m ->
                kardexBuilder.append("  - ${m.materia} (${m.clave}): Calificación ${m.calificacion}, Créditos ${m.creditos}, Evaluación: ${m.evaluacion}\n")
            }
        }
        
        val totalAprobados = localKardex.filter { it.calificacion.toIntOrNull()?.let { c -> c >= 70 } ?: (it.calificacion == "100") }.sumOf { it.creditos }

        val allTramites = tramiteDao.getAllTramites()
        val infoBuilder = StringBuilder()
        for (tramite in allTramites) {
            val requisitos = tramiteDao.getRequisitosByTramiteId(tramite.id)
            val pasos = tramiteDao.getPasosByTramiteId(tramite.id)
            
            infoBuilder.append("--- ${tramite.category}: ${tramite.title} ---\n")
            infoBuilder.append("Descripción: ${tramite.description}\n")
            if (requisitos.isNotEmpty()) infoBuilder.append("Requisitos: ${requisitos.joinToString(", ") { it.description }}\n")
            if (pasos.isNotEmpty()) infoBuilder.append("Pasos: ${pasos.joinToString(" -> ") { "${it.stepNumber}. ${it.title}" }}\n")
            infoBuilder.append("\n")
        }

        return """
            DATOS DE IDENTIDAD:
            Alumno: $nombreAlumno
            Control: $numControl
            Carrera: INGENIERIA EN SISTEMAS COMPUTACIONALES (ISIC-2010-224)
            Especialidad: Tecnologías Web y Móvil Aplicadas al Comercio Electrónico.

            KARDEX COMPLETO (REAL):
            ${kardexBuilder.toString()}
            
            REGLA DE EVALUACIÓN:
            - 'N/A' significa 'NO APROBÓ'. Es una materia REPROBADA.
            - Si el alumno pregunta qué materias reprobó, busca las que tienen calificación 'N/A' en el listado de arriba y lístalas.
            
            INFORMACIÓN OFICIAL ITTG:
            ${infoBuilder.toString()}
        """.trimIndent()
    }

    private fun extractContent(json: String): String {
        return try {
            // Regex mejorado para manejar comillas escapadas dentro del contenido
            val regex = "\"content\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"".toRegex()
            val match = regex.find(json)
            val content = match?.groupValues?.get(1)
            
            content?.replace("\\\\", "\\")
                ?.replace("\\\"", "\"")
                ?.replace("\\n", "\n")
                ?.replace("\\r", "\r")
                ?.replace("\\t", "\t") ?: "No pude procesar la respuesta."
        } catch (e: Exception) { "Error de formato al extraer respuesta" }
    }

    suspend fun getLocalChatHistory(userId: Int?): List<ChatMessageLocalEntity> = chatDao.getMessagesForUser(userId)
    suspend fun getKardex(userId: Int): ApiResult<KardexResponse> = withContext(Dispatchers.IO) {
        try {
            val localKardex = kardexDao.getKardex(userId)
            if (localKardex.isNotEmpty()) {
                val data = localKardex.map {
                    CalificacionDto(
                        periodo = it.periodo,
                        clave = it.clave,
                        materia = it.materia,
                        creditos = it.creditos,
                        calificacion = it.calificacion,
                        evaluacion = it.evaluacion,
                        observaciones = null
                    )
                }
                // Calcular promedio localmente
                val numericas = data.mapNotNull { it.calificacion.toDoubleOrNull() }
                val promedio = if (numericas.isNotEmpty()) numericas.average() else 0.0
                val creditosAprobados = data.filter { 
                    it.calificacion.toDoubleOrNull()?.let { c -> c >= 70 } ?: (it.calificacion == "AC") 
                }.sumOf { it.creditos }
                
                ApiResult.Success(KardexResponse(
                    success = true,
                    message = "Cargado desde control interno",
                    data = data,
                    promedioGeneral = promedio,
                    creditosTotales = 276, // Dato fijo ITTG
                    creditosAprobados = creditosAprobados
                ))
            } else {
                ApiResult.Error("No hay datos de Kardex en el control interno.")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al acceder al control interno: ${e.localizedMessage}")
        }
    }
    suspend fun resetPassword(token: String, newPassword: String): ApiResult<ResetPasswordResponse> = withContext(Dispatchers.IO) {
        ApiResult.Success(ResetPasswordResponse(true, "Contraseña actualizada localmente."))
    }

    suspend fun createSupportTicket(userId: Int?, name: String, email: String, category: String, description: String): ApiResult<SupportTicketResponse> = withContext(Dispatchers.IO) {
        try {
            // Aquí iría la llamada a la API real. 
            // Por ahora simulamos una posible falla de red si no hay API configurada.
            ApiResult.Success(SupportTicketResponse(true, "Solicitud guardada en el control interno del dispositivo.", 0))
        } catch (e: Exception) {
            ApiResult.Success(SupportTicketResponse(true, "Offline", null))
        }
    }

    suspend fun forgotPassword(email: String): ApiResult<ForgotPasswordResponse> = withContext(Dispatchers.IO) {
        ApiResult.Success(ForgotPasswordResponse(true, "Solicitud procesada internamente.", "TOKEN-LOCAL-123"))
    }
}
