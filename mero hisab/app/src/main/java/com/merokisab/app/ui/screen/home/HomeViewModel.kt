package com.merokisab.app.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.merokisab.app.data.database.AppDatabase
import com.merokisab.app.data.entity.Ledger
import com.merokisab.app.util.formatBs
import com.merokisab.app.util.formatAd
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    val ledgers: StateFlow<List<Ledger>> = db.ledgerDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createLedger(name: String, startingBalance: Double) {
        viewModelScope.launch {
            val now = java.util.Calendar.getInstance()
            val year = now.get(java.util.Calendar.YEAR)
            val month = now.get(java.util.Calendar.MONTH) + 1
            val day = now.get(java.util.Calendar.DAY_OF_MONTH)
            val adDate = formatAd(year, month, day)
            val bsDate = formatBs(year, month, day)  // simplified, actual conversion in util
            db.ledgerDao().insert(
                Ledger(
                    name = name.trim().ifEmpty { "Untitled" },
                    createdDateAD = adDate,
                    createdDateBS = bsDate,
                    lastEditedDateAD = adDate,
                    lastEditedDateBS = bsDate,
                    startingBalance = startingBalance,
                )
            )
        }
    }

    fun deleteLedger(ledger: Ledger) {
        viewModelScope.launch {
            db.ledgerDao().delete(ledger)
        }
    }
}