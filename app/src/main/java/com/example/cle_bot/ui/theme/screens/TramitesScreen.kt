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
import androidx.compose.ui.graphics.Color
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
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Trámites Disponibles", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar trámite", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn {
                    items(tramites) { tramite ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A1A1A)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onTramiteClick(tramite.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = tramite.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tramite.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
