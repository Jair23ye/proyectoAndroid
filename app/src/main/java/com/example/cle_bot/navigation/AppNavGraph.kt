package com.example.cle_bot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cle_bot.data.CleBotRepository
import com.example.cle_bot.data.SessionManager
import com.example.cle_bot.data.TramiteRepository
import com.example.cle_bot.data.UserDto
import com.example.cle_bot.data.local.CleBotDatabase
import com.example.cle_bot.ui.theme.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { CleBotRepository() }
    val tramiteRepository = remember { 
        TramiteRepository(dao = CleBotDatabase.getDatabase(context).tramiteDao()) 
    }
    val sessionManager = remember { SessionManager(context) }
    var currentUser by remember { 
        mutableStateOf<UserDto?>(sessionManager.getUser())
    }
    var recoveryToken by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) NavRoutes.CHAT else NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                repository         = repository,
                onLoginSuccess     = { user ->
                    sessionManager.saveSession(user)
                    currentUser = user
                    navController.navigate(NavRoutes.CHAT) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick    = { navController.navigate(NavRoutes.REGISTER) },
                onForgotPassword   = { navController.navigate(NavRoutes.FORGOT_PASSWORD) },
                onSupportClick     = { navController.navigate(NavRoutes.SUPPORT) }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                repository    = repository,
                onBack        = { navController.popBackStack() },
                onLoginClick  = { navController.navigate(NavRoutes.LOGIN) }
            )
        }
        composable(NavRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                repository     = repository,
                onBack         = { navController.popBackStack() },
                onTokenGenerated = { token -> recoveryToken = token },
                onResetPasswordClick = { navController.navigate(NavRoutes.RESET_PASSWORD) },
                onSupportClick = { navController.navigate(NavRoutes.SUPPORT) }
            )
        }
        composable(NavRoutes.RESET_PASSWORD) {
            ResetPasswordScreen(
                repository = repository,
                initialToken = recoveryToken,
                onBack = { navController.popBackStack() },
                onPasswordChanged = {
                    recoveryToken = ""
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(NavRoutes.SUPPORT) {
            SupportScreen(
                repository = repository,
                user = currentUser,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.FAQ) {
            FaqScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.CHAT) {
            ChatScreen(
                repository          = repository,
                user                = currentUser,
                onNavigateToFaq     = { navController.navigate(NavRoutes.FAQ) },
                onNavigateToSupport = { navController.navigate(NavRoutes.SUPPORT) },
                onNavigateToTramites = { navController.navigate(NavRoutes.TRAMITES) },
                onNavigateToKardex  = { navController.navigate(NavRoutes.KARDEX) },
                onLogout            = {
                    sessionManager.clearSession()
                    currentUser = null
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.CHAT) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(NavRoutes.TRAMITES) {
            TramitesScreen(
                repository = tramiteRepository,
                onBack = { navController.popBackStack() },
                onTramiteClick = { tramiteId -> 
                    navController.navigate(NavRoutes.createTramiteDetailRoute(tramiteId)) 
                }
            )
        }
        composable(NavRoutes.TRAMITE_DETAIL) { backStackEntry ->
            val tramiteIdStr = backStackEntry.arguments?.getString("tramiteId")
            val tramiteId = tramiteIdStr?.toIntOrNull() ?: 0
            val userId = currentUser?.id ?: 0
            
            TramiteDetailScreen(
                tramiteId = tramiteId,
                userId = userId,
                repository = tramiteRepository,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.KARDEX) {
            val userId = currentUser?.id ?: 0
            KardexScreen(
                userId = userId,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
