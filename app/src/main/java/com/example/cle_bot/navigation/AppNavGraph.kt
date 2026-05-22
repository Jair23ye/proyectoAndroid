package com.example.cle_bot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cle_bot.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginClick       = { navController.navigate(NavRoutes.CHAT) },
                onRegisterClick    = { navController.navigate(NavRoutes.REGISTER) },
                onForgotPassword   = { navController.navigate(NavRoutes.FORGOT_PASSWORD) },
                onSupportClick     = { navController.navigate(NavRoutes.SUPPORT) }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onBack        = { navController.popBackStack() },
                onLoginClick  = { navController.navigate(NavRoutes.LOGIN) }
            )
        }
        composable(NavRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBack         = { navController.popBackStack() },
                onSupportClick = { navController.navigate(NavRoutes.SUPPORT) }
            )
        }
        composable(NavRoutes.SUPPORT) {
            SupportScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.FAQ) {
            FaqScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.CHAT) {
            ChatScreen(
                onNavigateToFaq     = { navController.navigate(NavRoutes.FAQ) },
                onNavigateToSupport = { navController.navigate(NavRoutes.SUPPORT) },
                onLogout            = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.CHAT) { inclusive = true }
                    }
                }
            )
        }
    }
}