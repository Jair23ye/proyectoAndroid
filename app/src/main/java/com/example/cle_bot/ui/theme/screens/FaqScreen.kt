package com.example.cle_bot.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FaqItem(val question: String, val answer: String)

val faqItems = listOf(
    FaqItem(
        "¿Cómo recupero mi contraseña?",
        "Haz click en \"¿Olvidaste tu contraseña?\" en la pagina de inicio de sesion e ingresa tu correo instucional. Recibirás un enlace para crear una nueva contraseña."
    ),
    FaqItem(
        "¿Qué hago si no puedo crear una cuenta?",
        "Asegurate de usar tu correo institucional válido (@tuxtla.tecnm.mx) y que tu matricula este correcta. Si el problema persiste, contacta a soporte tecnico."
    ),
    FaqItem(
        "¿Cuál es el horario de atención?",
        "Nuestro equipo de soporte está disponible de Lunes a Viernes de 08:00 a 18:00 hrs. Los mensajes recibidos fuera de este horario seran atendidos al dia siguiente hábil."
    )
)

@Composable
fun FaqScreen(onBack: () -> Unit) {
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
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "PREGUNTAS FRECUENTES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            faqItems.forEach { item ->
                var expanded by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { expanded = !expanded },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.question, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        AnimatedVisibility(visible = expanded) {
                            Text(
                                item.answer,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}