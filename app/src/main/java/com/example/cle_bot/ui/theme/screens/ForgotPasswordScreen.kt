package com.example.cle_bot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.example.cle_bot.R
import androidx.compose.foundation.Image
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit, onSupportClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val blue = Color(0xFF3D5BF5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F3F8))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            Text("Volver al inicio", fontSize = 14.sp, color = Color.Gray)
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
            Text("Recuperar contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Ingresa tu correo con el que creaste la cuenta y te enviamos las instrucciones",
                fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

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
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue)
                    ) { Text("Enviar las instrucciones") }

                    Spacer(Modifier.height(16.dp))

                    // Tip card
                    Surface(
                        color = Color(0xFFEEF0FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(Modifier.padding(14.dp)) {
                            Icon(Icons.Outlined.Lightbulb, null, tint = blue)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Consejo: Asegurate de usar tu correo institucional en caso de ser alumno vigente o tu correo personal en caso de ser alumno externo.",
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row {
                Text("¿Sigues teniendo problemas? ", fontSize = 12.sp, color = Color.Gray)
                TextButton(onClick = onSupportClick, contentPadding = PaddingValues(0.dp)) {
                    Text("contacta a soporte", color = blue, fontSize = 12.sp)
                }
            }
        }
    }
}