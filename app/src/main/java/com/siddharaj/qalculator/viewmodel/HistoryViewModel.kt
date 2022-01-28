package com.siddharaj.qalculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.siddharaj.qalculator.model.HistoryModel
import com.siddharaj.qalculator.database.HistoryDatabase
import com.siddharaj.qalculator.database.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
   val history:LiveData<List<HistoryModel>>
   private val repository: HistoryRepository
   init {
       val database = HistoryDatabase.getDatabase(application)
       val dao = database.getHistory()
       repository= HistoryRepository(dao)
       history=repository.allHistory
   }

    fun insert(historyModel: HistoryModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(historyModel)
    }
    fun delete()=viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}