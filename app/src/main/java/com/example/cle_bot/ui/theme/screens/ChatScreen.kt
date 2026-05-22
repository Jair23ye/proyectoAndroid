package com.example.cle_bot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.example.cle_bot.R
import androidx.compose.foundation.Image

data class ChatMessage(val text: String, val isBot: Boolean)
data class QuickAction(val icon: ImageVector, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToFaq: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onLogout: () -> Unit
) {
    val blue = Color(0xFF3D5BF5)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("¡Hola! Soy tu asistente virtual CLEbot. ¿En qué puedo ayudarte hoy?", true)
        )
    }

    val quickActions = listOf(
        QuickAction(Icons.Default.Description, "Constancias"),
        QuickAction(Icons.Default.CalendarMonth, "Horarios"),
        QuickAction(Icons.Default.CreditCard, "Pagos"),
        QuickAction(Icons.Default.MenuBook, "Libros"),
        QuickAction(Icons.Default.School, "Liberación"),
        QuickAction(Icons.Default.Edit, "Reinscripción")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.78f)) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_clebot),
                        contentDescription = "CLEbot logo",
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("CLEbot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("En línea", color = Color(0xFF4CAF50), fontSize = 12.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { scope.launch { drawerState.close() } }) {
                        Icon(Icons.Default.Close, null)
                    }
                }

                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                listOf(
                    Triple(Icons.Default.Chat, "Chat actual") { scope.launch { drawerState.close() } },
                    Triple(Icons.Default.History, "Historial") { },
                    Triple(Icons.Default.Help, "Preguntas frecuentes") { onNavigateToFaq(); scope.launch { drawerState.close() } },
                    Triple(Icons.Default.Settings, "Configuración") { }
                ).forEach { (icon, label, action) ->
                    NavigationDrawerItem(
                        icon = { Icon(icon, null, tint = blue) },
                        label = { Text(label) },
                        selected = false,
                        onClick = { action() },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider()

                // Footer del drawer
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(40.dp).background(Color.Gray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("E", color = Color.White, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Estudiante", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("estudiante@tuxtla.tecnm.mx", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_clebot),
                                contentDescription = "CLEbot logo",
                                modifier = Modifier.size(90.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("CLEbot", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("En línea", color = Color(0xFF4CAF50), fontSize = 12.sp)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Escribe aquí....") },
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    messages.add(ChatMessage(inputText, false))
                                    inputText = ""
                                    // TODO: llamar al agente de IA aquí
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, null, tint = blue)
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F3F8))
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Grid de acciones rápidas
                item {
                    Text(
                        "¿Qué trámite necesitas realizar?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    Text(
                        "Selecciona una opción o escribe tu consulta",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // 3 columnas de 2 botones cada fila
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        quickActions.chunked(2).forEach { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { action ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.4f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        onClick = {
                                            messages.add(ChatMessage(action.label, false))
                                            // TODO: enviar al agente
                                        }
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Box(
                                                Modifier
                                                    .size(44.dp)
                                                    .background(Color(0xFFEEF0FF), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(action.icon, null, tint = blue, modifier = Modifier.size(24.dp))
                                            }
                                            Spacer(Modifier.height(6.dp))
                                            Text(action.label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Mensajes del chat
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val blue = Color(0xFF3D5BF5)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isBot) Arrangement.Start else Arrangement.End
    ) {
        if (message.isBot) {
            Image(
                painter = painterResource(id = R.drawable.logo_clebot),
                contentDescription = "CLEbot logo",
                modifier = Modifier.size(90.dp)
            )
            Spacer(Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (message.isBot) Alignment.Start else Alignment.End) {
            Surface(
                color = if (message.isBot) Color.White else blue,
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomEnd = if (message.isBot) 16.dp else 4.dp,
                    bottomStart = if (message.isBot) 4.dp else 16.dp
                ),
                shadowElevation = 1.dp
            ) {
                Text(
                    message.text,
                    modifier = Modifier.padding(12.dp),
                    color = if (message.isBot) Color.Black else Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}