package com.siddharaj.qalculator.database


import androidx.lifecycle.LiveData
import androidx.room.*
import com.siddharaj.qalculator.model.HistoryModel

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: HistoryModel)

    @Query("SELECT * from history_table")
    fun readHistory(): LiveData<List<HistoryModel>>

    @Query("DELETE from history_table")
    suspend fun deleteHistory()
}