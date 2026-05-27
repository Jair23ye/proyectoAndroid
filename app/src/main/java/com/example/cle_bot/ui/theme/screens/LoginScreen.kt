package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.example.cle_bot.R
import androidx.compose.foundation.Image
import com.example.cle_bot.data.ApiResult
import com.example.cle_bot.data.CleBotRepository
import com.example.cle_bot.data.UserDto
import kotlinx.coroutines.launch

private val AppBlue = Color(0xFF3D5BF5)

@Composable
fun LoginScreen(
    repository: CleBotRepository,
    onLoginSuccess: (UserDto) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPassword: () -> Unit,
    onSupportClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submitLogin() {
        if (email.isBlank() || password.isBlank()) {
            feedback = "Ingresa correo y contraseña."
            return
        }

        scope.launch {
            isLoading = true
            feedback = null
            when (val result = repository.login(email, password)) {
                is ApiResult.Success -> {
                    val user = result.data.user
                    if (user != null) {
                        onLoginSuccess(user)
                    } else {
                        feedback = "No se recibio la informacion del usuario."
                    }
                }
                is ApiResult.Error -> feedback = result.message
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F3F8))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo placeholder — reemplaza con tu Image() cuando tengas el asset
        Image(
            painter = painterResource(id = R.drawable.logo_clebot),
            contentDescription = "CLEbot logo",
            modifier = Modifier.size(90.dp)
        )

        Spacer(Modifier.height(12.dp))
        Text("CLEbot", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            "Inicia sesión con tu correo institucional",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        // Card del formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Correo", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ingresa tu correo Institucional") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(14.dp))
                Text("Contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ingresa tu Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onForgotPassword) {
                        Text("¿Olvidaste tu contraseña?", color = AppBlue, fontSize = 13.sp)
                    }
                }

                feedback?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { submitLogin() },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppBlue)
                ) {
                    Text(if (isLoading) "Iniciando..." else "Iniciar sesión", fontSize = 16.sp)
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(Modifier.weight(1f))
                    Text("  o  ", color = Color.Gray, fontSize = 12.sp)
                    HorizontalDivider(Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppBlue)
                ) {
                    Text("Crear nueva cuenta", fontSize = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Row {
            Text("¿Tienes problemas para acceder? ", fontSize = 12.sp, color = Color.Gray)
            TextButton(onClick = onSupportClick, contentPadding = PaddingValues(0.dp)) {
                Text("contacta a soporte", color = AppBlue, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Al iniciar sesión aceptas nuestros terminos y condiciones",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
