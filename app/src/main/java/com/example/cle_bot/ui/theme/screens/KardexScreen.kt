package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cle_bot.data.ApiResult
import com.example.cle_bot.data.CalificacionDto
import com.example.cle_bot.data.CleBotRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(
    userId: Int,
    repository: CleBotRepository,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var kardexData by remember { mutableStateOf<List<CalificacionDto>>(emptyList()) }
    var promedioGeneral by remember { mutableStateOf<Double?>(null) }
    var creditosTotales by remember { mutableStateOf<Int?>(null) }
    var creditosAprobados by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(userId) {
        val result = repository.getKardex(userId)
        when (result) {
            is ApiResult.Success -> {
                kardexData = result.data.data ?: emptyList()
                promedioGeneral = result.data.promedioGeneral
                creditosTotales = result.data.creditosTotales
                creditosAprobados = result.data.creditosAprobados
                isLoading = false
            }
            is ApiResult.Error -> {
                errorMessage = result.message
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Mi Kardex Académico", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color.Black)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (kardexData.isEmpty()) {
                Text(
                    text = "No hay registros académicos disponibles.",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val groupedData = kardexData.groupBy { it.periodo }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        KardexSummaryHeader(promedioGeneral, creditosTotales, creditosAprobados)
                    }
                    groupedData.forEach { (periodo, materias) ->
                        item {
                            PeriodHeader(periodo)
                        }
                        items(materias) { materia ->
                            KardexRow(materia)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KardexSummaryHeader(promedio: Double?, total: Int?, aprobados: Int?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Promedio", fontSize = 12.sp, color = Color.LightGray)
                Text(
                    text = promedio?.toString() ?: "0.0",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D5BF5)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Créditos", fontSize = 12.sp, color = Color.LightGray)
                Text(
                    text = "${aprobados ?: 0} / ${total ?: 0}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PeriodHeader(periodo: String) {
    Surface(
        color = Color(0xFF2A2D3F),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = periodo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun KardexRow(materia: CalificacionDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = materia.materia,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = materia.calificacion,
                    fontSize = 18.sp,
                    color = if (materia.calificacion.toIntOrNull() ?: 0 >= 70) 
                            Color(0xFF4CAF50) else Color(0xFFFF5252),
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Clave: ${materia.clave} | Créditos: ${materia.creditos}",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                Text(
                    text = materia.evaluacion,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            
            if (!materia.observaciones.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Obs: ${materia.observaciones}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
