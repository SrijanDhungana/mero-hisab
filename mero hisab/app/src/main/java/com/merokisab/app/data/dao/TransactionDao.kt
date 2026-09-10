package com.merokisab.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.merokisab.app.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE ledgerId = :ledgerId ORDER BY dateAD DESC, id DESC")
    fun observeAll(ledgerId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun get(id: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE ledgerId = :ledgerId")
    suspend fun deleteAll(ledgerId: Int)

    @Query("SELECT COUNT(*) FROM transactions WHERE ledgerId = :ledgerId")
    suspend fun count(ledgerId: Int): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE ledgerId = :ledgerId AND type = 'INCOME' AND dateAD >= :from AND dateAD <= :to")
    suspend fun countIncomeInRange(ledgerId: Int, from: String, to: String): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE ledgerId = :ledgerId AND type = 'EXPENSE' AND dateAD >= :from AND dateAD <= :to")
    suspend fun countExpenseInRange(ledgerId: Int, from: String, to: String): Int
}
