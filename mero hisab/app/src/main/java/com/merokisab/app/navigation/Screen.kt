package com.merokisab.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Ledger : Screen("ledger/{ledgerId}") {
        fun createRoute(id: Int) = "ledger/$id"
    }
    object Analysis : Screen("analysis/{ledgerId}") {
        fun createRoute(id: Int) = "analysis/$id"
    }
}