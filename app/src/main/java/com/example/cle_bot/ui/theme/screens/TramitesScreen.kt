package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.cle_bot.data.TramiteRepository
import com.example.cle_bot.data.local.TramiteEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TramitesScreen(
    repository: TramiteRepository,
    onBack: () -> Unit,
    onTramiteClick: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var tramites by remember { mutableStateOf<List<TramiteEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(searchQuery) {
        isLoading = true
        tramites = repository.getTramites(search = searchQuery.takeIf { it.isNotBlank() })
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trámites Disponibles") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar trámite") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(tramites) { tramite ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onTramiteClick(tramite.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = tramite.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = tramite.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
