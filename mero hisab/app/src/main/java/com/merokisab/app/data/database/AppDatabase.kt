package com.merokisab.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.merokisab.app.data.dao.LedgerDao
import com.merokisab.app.data.dao.TransactionDao
import com.merokisab.app.data.entity.Ledger
import com.merokisab.app.data.entity.Transaction

@Database(
    entities = [Ledger::class, Transaction::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ledgerDao(): LedgerDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mero_kisab.db",
                ).build().also { instance = it }
            }
    }
}
