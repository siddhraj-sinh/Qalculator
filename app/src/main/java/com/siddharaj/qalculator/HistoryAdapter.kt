package com.siddharaj.qalculator

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.siddharaj.qalculator.databinding.ItemHistoryLayoutBinding
import com.siddharaj.qalculator.model.HistoryModel
import com.siddharaj.qalculator.viewmodel.HistoryViewModel

class HistoryAdapter(
    private val context: Context,
    private val items: List<HistoryModel>,
    private val cellClickListener: CellClickListener,
    val mHistoryViewModel: HistoryViewModel
):RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding:ItemHistoryLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
           ItemHistoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
       val currentItem = items[position]
        if (position % 2 == 0) {
           holder.binding.itemLl.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.azure_blue
                )
            )
        }
        /*else {
            holder.binding.itemLl.setBackgroundColor(ContextCompat.getColor(context, R.color.my_black))
        }*/
        holder.binding.itemTvExpression.text = currentItem.expression
        holder.binding.itemTvOutput.text = currentItem.result
        holder.binding.itemLl.setOnClickListener{
            cellClickListener.onCellClickListener(currentItem)
        }
    }

    fun deleteAll(){
        mHistoryViewModel.delete()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }


}
