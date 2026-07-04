package com.app.cashtrackapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.cashtrackapp.screens.ui.EntriesScreen
import com.app.cashtrackapp.screens.ui.transaction.TransactionsScreen
import com.app.cashtrackapp.ui.theme.CashTrackAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CashTrackAppTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(
                        onSplashComplete = {
                            showSplash = false
                        }
                    )
                } else {
                    Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
                        val navController = rememberNavController()

                        NavHost(
                            navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("home") {
                                EntriesScreen(
                                    onNavigateToTransactionsScreen = {
                                        navController.navigate("transactions")
                                    },
                                )
                            }

                            composable("transactions") {
                                TransactionsScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
