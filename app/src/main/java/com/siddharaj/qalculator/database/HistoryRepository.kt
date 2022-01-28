package com.siddharaj.qalculator.database


import androidx.lifecycle.LiveData
import com.siddharaj.qalculator.model.HistoryModel

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: LiveData<List<HistoryModel>> = historyDao.readHistory()

    suspend fun insert(historyModel: HistoryModel){
        historyDao.insertHistory(historyModel)
    }
    suspend fun deleteAll(){
        historyDao.deleteHistory()
    }

}