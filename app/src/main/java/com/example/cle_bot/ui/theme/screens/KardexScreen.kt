package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
        topBar = {
            TopAppBar(
                title = { Text("Mi Kardex Académico") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (kardexData.isEmpty()) {
                Text(
                    text = "No hay registros académicos disponibles.",
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Promedio", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = promedio?.toString() ?: "0.0",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Créditos", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "${aprobados ?: 0} / ${total ?: 0}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PeriodHeader(periodo: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(
            text = periodo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun KardexRow(materia: CalificacionDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = materia.materia,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = materia.calificacion,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (materia.calificacion.toIntOrNull() ?: 0 >= 70) 
                            Color(0xFF2E7D32) else Color.Red,
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
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = materia.evaluacion,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            
            if (!materia.observaciones.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Obs: ${materia.observaciones}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
