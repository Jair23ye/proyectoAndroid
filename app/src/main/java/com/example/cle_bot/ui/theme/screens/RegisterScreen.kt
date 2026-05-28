package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.example.cle_bot.R
import androidx.compose.foundation.Image
import com.example.cle_bot.data.ApiResult
import com.example.cle_bot.data.CleBotRepository
import kotlinx.coroutines.launch

private val AppBlue2 = Color(0xFF3D5BF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    repository: CleBotRepository,
    onBack: () -> Unit,
    onLoginClick: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var numControl by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submitRegister() {
        if (nombre.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            feedback = "Completa los campos obligatorios."
            return
        }
        if (password != confirmPassword) {
            feedback = "Las contraseñas no coinciden."
            return
        }
        if (password.length < 6) {
            feedback = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        scope.launch {
            isLoading = true
            feedback = null
            when (val result = repository.register(nombre, numControl, email, password)) {
                is ApiResult.Success -> onLoginClick()
                is ApiResult.Error -> feedback = result.message
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
        // Top bar
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
            }
            Text("Volver al inicio", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
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
            Text("Crear cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Completa tus datos para registrarte", fontSize = 13.sp, color = Color.LightGray)
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    listOf(
                        Triple("Nombre", nombre, { v: String -> nombre = v }),
                        Triple("Numero de control (en caso de ser vigente)", numControl, { v: String -> numControl = v }),
                        Triple("Correo", email, { v: String -> email = v })
                    ).forEach { (label, value, setter) ->
                        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = value,
                            onValueChange = setter,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(label, color = Color.Gray) },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray,
                                focusedBorderColor = AppBlue2,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        Spacer(Modifier.height(14.dp))
                    }

                    Text("Contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Crea una nueva contraseña", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            focusedBorderColor = AppBlue2,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(Modifier.height(14.dp))

                    Text("Confirma la contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Repite la contraseña", color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            focusedBorderColor = AppBlue2,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(Modifier.height(20.dp))

                    feedback?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        Spacer(Modifier.height(10.dp))
                    }

                    Button(
                        onClick = { submitRegister() },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppBlue2)
                    ) {
                        Text(if (isLoading) "Creando..." else "Crear cuenta", fontSize = 16.sp, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes una cuenta? ", fontSize = 13.sp, color = Color.White)
                TextButton(onClick = onLoginClick, contentPadding = PaddingValues(0.dp)) {
                    Text("Inicia sesión", color = AppBlue2, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
