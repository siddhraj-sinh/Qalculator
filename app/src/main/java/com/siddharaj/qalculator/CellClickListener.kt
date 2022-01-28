package com.siddharaj.qalculator

import com.siddharaj.qalculator.model.HistoryModel


interface CellClickListener {
    fun onCellClickListener(data: HistoryModel)
}