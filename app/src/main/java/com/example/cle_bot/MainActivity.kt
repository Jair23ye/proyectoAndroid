package com.example.cle_bot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.cle_bot.navigation.AppNavGraph
import com.example.cle_bot.ui.theme.Cle_botTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cle_botTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}