package com.siddharaj.qalculator.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int, var expression: String, var result: String
)
