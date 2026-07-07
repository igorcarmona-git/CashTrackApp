package com.app.cashtrackapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.cashtrackapp.screens.ui.EntriesScreen
import com.app.cashtrackapp.screens.ui.transaction.TransactionsScreen

private object CashTrackRoutes {
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
}

@Composable
fun CashTrackNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CashTrackRoutes.HOME,
        modifier = modifier
    ) {
        composable(CashTrackRoutes.HOME) {
            EntriesScreen(
                onNavigateToTransactionsScreen = {
                    navController.navigate(CashTrackRoutes.TRANSACTIONS)
                }
            )
        }

        composable(CashTrackRoutes.TRANSACTIONS) {
            TransactionsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
