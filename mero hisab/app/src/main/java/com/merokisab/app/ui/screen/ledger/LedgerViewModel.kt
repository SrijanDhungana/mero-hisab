package com.merokisab.app.ui.screen.ledger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merokisab.app.data.database.AppDatabase
import com.merokisab.app.data.entity.Ledger
import com.merokisab.app.data.entity.Transaction
import com.merokisab.app.util.formatAd
import com.merokisab.app.util.formatBs
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LedgerUiState(
    val ledger: Ledger?,
    val transactions: List<Transaction>,
    val balance: Double,
)

class LedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    fun observeLedger(ledgerId: Int): StateFlow<LedgerUiState> {
        val ledgerFlow = db.ledgerDao().observeById(ledgerId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        val txFlow = db.transactionDao().observeAll(ledgerId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        return combine(ledgerFlow, txFlow) { ledger, txs ->
            LedgerUiState(
                ledger = ledger,
                transactions = txs,
                balance = ledger?.startingBalance?.plus(txs.sumOf { if (it.type == "INCOME") it.amount else -it.amount }) ?: 0.0,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LedgerUiState(null, emptyList(), 0.0))
    }

    fun saveTransaction(tx: Transaction) {
        viewModelScope.launch {
            db.transactionDao().insert(tx)
            val ledger = db.ledgerDao().get(tx.ledgerId)
            if (ledger != null) {
                val now = java.util.Calendar.getInstance()
                val y = now.get(java.util.Calendar.YEAR)
                val m = now.get(java.util.Calendar.MONTH) + 1
                val d = now.get(java.util.Calendar.DAY_OF_MONTH)
                db.ledgerDao().update(ledger.copy(lastEditedDateAD = formatAd(y, m, d), lastEditedDateBS = formatBs(y, m, d)))
            }
        }
    }

    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch {
            db.transactionDao().delete(tx)
        }
    }

    fun renameLedger(ledger: Ledger, newName: String) {
        viewModelScope.launch {
            val now = java.util.Calendar.getInstance()
            val y = now.get(java.util.Calendar.YEAR)
            val m = now.get(java.util.Calendar.MONTH) + 1
            val d = now.get(java.util.Calendar.DAY_OF_MONTH)
            db.ledgerDao().update(ledger.copy(name = newName, lastEditedDateAD = formatAd(y, m, d), lastEditedDateBS = formatBs(y, m, d)))
        }
    }
}