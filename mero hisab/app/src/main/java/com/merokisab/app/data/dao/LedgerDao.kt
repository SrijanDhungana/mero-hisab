package com.merokisab.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.merokisab.app.data.entity.Ledger
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {
    @Query("SELECT * FROM ledgers ORDER BY lastEditedDateAD DESC")
    fun observeAll(): Flow<List<Ledger>>

    @Query("SELECT * FROM ledgers WHERE id = :id")
    fun observeById(id: Int): Flow<Ledger?>

    @Query("SELECT * FROM ledgers WHERE id = :id")
    suspend fun get(id: Int): Ledger?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ledger: Ledger): Long

    @Update
    suspend fun update(ledger: Ledger)

    @Delete
    suspend fun delete(ledger: Ledger)

    @Query("DELETE FROM ledgers WHERE id = :id")
    suspend fun deleteById(id: Int)
}