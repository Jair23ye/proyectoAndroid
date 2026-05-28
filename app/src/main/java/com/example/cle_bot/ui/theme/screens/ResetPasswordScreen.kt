package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cle_bot.R
import com.example.cle_bot.data.ApiResult
import com.example.cle_bot.data.CleBotRepository
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(
    repository: CleBotRepository,
    initialToken: String,
    onBack: () -> Unit,
    onPasswordChanged: () -> Unit
) {
    val blue = Color(0xFF3D5BF5)
    val scope = rememberCoroutineScope()
    var token by remember(initialToken) { mutableStateOf(initialToken) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun submitReset() {
        if (token.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            feedback = "Completa el token y la nueva contraseña."
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
            when (val result = repository.resetPassword(token, password)) {
                is ApiResult.Success -> onPasswordChanged()
                is ApiResult.Error -> feedback = result.message
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F3F8))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text("Volver", fontSize = 14.sp, color = Color.Gray)
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
            Text("Cambiar contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(8.dp))
            Text(
                "Usa el token generado para registrar una nueva contraseña.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Token", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Pega tu token", color = Color.Gray) },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(14.dp))
                    Text("Nueva contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nueva contraseña", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(14.dp))
                    Text("Confirmar contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Repite la contraseña", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(16.dp))
                    feedback?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        Spacer(Modifier.height(10.dp))
                    }

                    Button(
                        onClick = { submitReset() },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue)
                    ) {
                        Text(if (isLoading) "Guardando..." else "Guardar nueva contraseña")
                    }
                }
            }
        }
    }
}
