package com.merokisab.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = Ledger::class,
        parentColumns = ["id"],
        childColumns = ["ledgerId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(value = ["ledgerId", "dateAD"])],
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ledgerId: Int,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val amount: Double,
    val dateAD: String,
    val dateBS: String,
    val remark: String = "",
)
