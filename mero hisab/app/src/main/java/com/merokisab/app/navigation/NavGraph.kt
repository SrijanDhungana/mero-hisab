package com.merokisab.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.merokisab.app.ui.screen.analysis.AnalysisScreen
import com.merokisab.app.ui.screen.home.HomeScreen
import com.merokisab.app.ui.screen.ledger.LedgerScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    onOpenLedger: (Int) -> Unit,
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(onOpenLedger = onOpenLedger)
        }
        composable(Screen.Ledger.route) { backStackEntry ->
            val ledgerId = backStackEntry.arguments?.getString("ledgerId")?.toIntOrNull() ?: return@composable
            LedgerScreen(ledgerId = ledgerId, navController = navController)
        }
        composable(Screen.Analysis.route) { backStackEntry ->
            val ledgerId = backStackEntry.arguments?.getString("ledgerId")?.toIntOrNull() ?: return@composable
            AnalysisScreen(ledgerId = ledgerId, navController = navController)
        }
    }
}