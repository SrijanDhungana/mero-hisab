package com.merokisab.app.ui.screen.analysis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merokisab.app.data.database.AppDatabase
import com.merokisab.app.data.entity.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AnalysisUiState(
    val balance: Double,
    val incomeTotal: Double,
    val expenseTotal: Double,
    val incomeByCategory: Map<String, Double>,
    val expenseByCategory: Map<String, Double>,
)

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    fun observe(ledgerId: Int): StateFlow<AnalysisUiState> {
        val txFlow = db.transactionDao().observeAll(ledgerId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        return txFlow.map { txs ->
            val income = txs.filter { it.type == "INCOME" }
            val expense = txs.filter { it.type == "EXPENSE" }
            AnalysisUiState(
                balance = income.sumOf { it.amount } - expense.sumOf { it.amount },
                incomeTotal = income.sumOf { it.amount },
                expenseTotal = expense.sumOf { it.amount },
                incomeByCategory = income.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } },
                expenseByCategory = expense.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalysisUiState(0.0, 0.0, 0.0, emptyMap(), emptyMap()))
    }
}