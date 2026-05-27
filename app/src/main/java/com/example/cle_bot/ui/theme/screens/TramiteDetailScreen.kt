package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.cle_bot.data.TramiteRepository
import com.example.cle_bot.data.TramiteDetailResult
import com.example.cle_bot.data.local.ProgresoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TramiteDetailScreen(
    tramiteId: Int,
    userId: Int,
    repository: TramiteRepository,
    onBack: () -> Unit
) {
    var detail by remember { mutableStateOf<TramiteDetailResult?>(null) }
    var progresoList by remember { mutableStateOf<List<ProgresoEntity>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tramiteId) {
        detail = repository.getTramiteDetail(tramiteId)
        progresoList = repository.getProgreso(userId, tramiteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Trámite") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        detail?.let { data ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                item {
                    Text(text = data.tramite.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = data.tramite.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Requisitos", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(data.requisitos) { req ->
                    Text(text = "• ${req.description} ${if(req.isMandatory) "(Obligatorio)" else "(Opcional)"}", style = MaterialTheme.typography.bodyMedium)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Pasos", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(data.pasos) { paso ->
                    val isCompleted = progresoList.find { it.pasoId == paso.id }?.isCompleted ?: false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { checked ->
                                coroutineScope.launch {
                                    repository.saveProgreso(userId, tramiteId, paso.id, checked)
                                    progresoList = repository.getProgreso(userId, tramiteId)
                                }
                            }
                        )
                        Column {
                            Text(text = "Paso ${paso.stepNumber}: ${paso.title}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = paso.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
