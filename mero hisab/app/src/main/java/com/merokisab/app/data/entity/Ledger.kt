package com.merokisab.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledgers")
data class Ledger(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdDateAD: String,
    val createdDateBS: String,
    val lastEditedDateAD: String,
    val lastEditedDateBS: String,
    val startingBalance: Double,
)
