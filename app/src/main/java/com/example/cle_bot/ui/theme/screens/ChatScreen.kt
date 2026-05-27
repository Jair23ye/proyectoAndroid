package com.example.cle_bot.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.geometry.Rect
import com.example.cle_bot.R
import androidx.compose.foundation.Image
import com.example.cle_bot.data.ApiResult
import com.example.cle_bot.data.CleBotRepository
import com.example.cle_bot.data.UserDto

data class ChatMessage(val text: String, val isBot: Boolean)
data class QuickAction(val icon: ImageVector, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    repository: CleBotRepository,
    user: UserDto?,
    onNavigateToFaq: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToTramites: () -> Unit,
    onNavigateToKardex: () -> Unit,
    onLogout: () -> Unit
) {
    val blue = Color(0xFF3D5BF5)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Objeto para ocultar la barra de herramientas de texto del sistema
    val emptyTextToolbar = object : TextToolbar {
        override val status: TextToolbarStatus = TextToolbarStatus.Hidden
        override fun hide() {}
        override fun showMenu(
            rect: Rect,
            onCopyRequested: (() -> Unit)?,
            onPasteRequested: (() -> Unit)?,
            onCutRequested: (() -> Unit)?,
            onSelectAllRequested: (() -> Unit)?
        ) {}
    }

    var inputText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("¡Hola! Soy tu asistente virtual CLEbot. ¿En qué puedo ayudarte hoy?", true)
        )
    }

    // Auto-scroll al final cuando hay mensajes nuevos
    LaunchedEffect(messages.size, isSending) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage(text: String) {
        val message = text.trim()
        if (message.isBlank() || isSending) return

        messages.add(ChatMessage(message, false))
        inputText = ""

        scope.launch {
            isSending = true
            when (val result = repository.sendChatMessage(user?.id, message)) {
                is ApiResult.Success -> {
                    messages.add(
                        ChatMessage(
                            result.data.botReply ?: "No pude generar una respuesta en este momento.",
                            true
                        )
                    )
                }
                is ApiResult.Error -> messages.add(ChatMessage(result.message, true))
            }
            isSending = false
        }
    }

    val quickActions = listOf(
        QuickAction(Icons.Default.Description, "Constancias"),
        QuickAction(Icons.Default.CalendarMonth, "Horarios"),
        QuickAction(Icons.Default.CreditCard, "Pagos"),
        QuickAction(Icons.Default.MenuBook, "Libros"),
        QuickAction(Icons.Default.School, "Liberación"),
        QuickAction(Icons.Default.Edit, "Reinscripción")
    )
    val displayName = user?.name ?: "Estudiante"
    val displayEmail = user?.email ?: "Sin sesion activa"
    val avatarInitial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "E"

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
                    Triple(Icons.Default.School, "Mi Kardex Académico") { onNavigateToKardex(); scope.launch { drawerState.close() } },
                    Triple(Icons.Default.Assignment, "Trámites Paso a Paso") { onNavigateToTramites(); scope.launch { drawerState.close() } },
                    Triple(Icons.Default.Help, "Preguntas frecuentes") { onNavigateToFaq(); scope.launch { drawerState.close() } },
                    Triple(Icons.Default.SupportAgent, "Soporte") { onNavigateToSupport(); scope.launch { drawerState.close() } },
                    Triple(Icons.Default.Settings, "Configuración") { },
                    Triple(Icons.Default.Logout, "Cerrar sesión") { onLogout() }
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
                    ) { Text(avatarInitial, color = Color.White, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(displayName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(displayEmail, fontSize = 11.sp, color = Color.Gray)
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
                        CompositionLocalProvider(LocalTextToolbar provides emptyTextToolbar) {
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Escribe aquí....") },
                                shape = RoundedCornerShape(24.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    autoCorrectEnabled = false,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Send
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = { sendMessage(inputText) }
                                )
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { sendMessage(inputText) },
                            enabled = !isSending
                        ) {
                            Icon(Icons.Default.Send, null, tint = blue)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F3F8))
                    .padding(paddingValues)
            ) {
                // El LazyColumn ahora solo contiene los mensajes, no los botones rápidos
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    // Mensajes del chat
                    items(messages) { msg ->
                        ChatBubble(msg)
                    }
                    if (isSending) {
                        item {
                            ChatBubble(ChatMessage("Escribiendo respuesta...", true))
                        }
                    }
                }

                // Contenedor fijo para las opciones/acciones rápidas
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "¿Qué trámite necesitas realizar?",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        
                        // Grid de acciones rápidas (2 filas x 3 columnas para que sea más compacto)
                        quickActions.chunked(3).forEach { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { action ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(70.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF)),
                                        onClick = { sendMessage(action.label) }
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(action.icon, null, tint = blue, modifier = Modifier.size(20.dp))
                                            Spacer(Modifier.height(4.dp))
                                            Text(action.label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(blue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_clebot),
                    contentDescription = "CLEbot logo",
                    modifier = Modifier.size(28.dp)
                )
            }
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
