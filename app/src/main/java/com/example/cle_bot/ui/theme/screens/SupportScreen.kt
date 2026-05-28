package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.cle_bot.R
import androidx.compose.foundation.Image
import com.example.cle_bot.data.ApiResult
import com.example.cle_bot.data.CleBotRepository
import com.example.cle_bot.data.UserDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    repository: CleBotRepository,
    user: UserDto?,
    onBack: () -> Unit
) {
    val blue = Color(0xFF3D5BF5)
    var nombre by remember(user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var email by remember(user?.email) { mutableStateOf(user?.email.orEmpty()) }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isOnline by remember { mutableStateOf(true) }
    val categorias = listOf("Acceso / Login", "Pagos", "Constancias", "Horarios", "Otro")
    val scope = rememberCoroutineScope()

    fun submitTicket() {
        if (nombre.isBlank() || email.isBlank() || categoria.isBlank() || descripcion.isBlank()) {
            feedback = "Completa todos los campos."
            return
        }

        scope.launch {
            isLoading = true
            feedback = null
            when (
                val result = repository.createSupportTicket(
                    userId = user?.id,
                    name = nombre,
                    email = email,
                    category = categoria,
                    description = descripcion
                )
            ) {
                is ApiResult.Success -> {
                    val isResultOffline = result.data.message == "Offline"
                    isOnline = !isResultOffline
                    feedback = if (isResultOffline) {
                        "El sistema de soporte está fuera de línea. Tu solicitud se guardará localmente."
                    } else {
                        result.data.message ?: "Solicitud enviada correctamente."
                    }
                    categoria = ""
                    descripcion = ""
                }
                is ApiResult.Error -> {
                    isOnline = false
                    feedback = result.message
                }
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
            Text("Volver al inicio", fontSize = 14.sp, color = Color.LightGray)
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_clebot),
                contentDescription = "CLEbot logo",
                modifier = Modifier.size(90.dp)
            )

            Spacer(Modifier.height(12.dp))
            Text("Centro de soporte", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (isOnline) Color(0xFF4CAF50) else Color.Gray, CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    if (isOnline) "En línea" else "Fuera de línea",
                    fontSize = 12.sp,
                    color = if (isOnline) Color(0xFF4CAF50) else Color.Gray
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Estamos aquí para ayudarte en cualquier problema",
                fontSize = 13.sp, color = Color.LightGray, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Enviar solicitud a soporte", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(14.dp))

                    Text("Nombre", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nombre, onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nombre completo", color = Color.Gray) },
                        shape = RoundedCornerShape(10.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = blue,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(Modifier.height(14.dp))

                    Text("Correo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ingresa tu correo", color = Color.Gray) },
                        shape = RoundedCornerShape(10.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = blue,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(Modifier.height(14.dp))

                    Text("Categoria del problema", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                            placeholder = { Text("Seleccione una categoria", color = Color.Gray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = blue,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFF1E1E1E))
                        ) {
                            categorias.forEach {
                                DropdownMenuItem(
                                    text = { Text(it, color = Color.White) },
                                    onClick = { categoria = it; expanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    Text("Describe tu problema", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = descripcion, onValueChange = { descripcion = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Cuentanos que estas experimentando...", color = Color.Gray) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = blue,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(Modifier.height(20.dp))

                    feedback?.let { message ->
                        Text(
                            message,
                            color = if (message.startsWith("Solicitud enviada")) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(10.dp))
                    }

                    Button(
                        onClick = { submitTicket() },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isLoading) "Enviando..." else "Enviar solicitud")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
