package com.example.snapoil.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapoil.R

class InfoAdapter(val infoList: List<Any>) : RecyclerView.Adapter<InfoAdapter.InfoHolder>() {
    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoHolder {
        return InfoHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.info_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InfoHolder, position: Int) {
        holder.setInfoData(infoList[position])
        holder.itemView.setSelected(selectedPosition == position)
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    fun getSelected(): Any? {
        if (selectedPosition >= 0) {
            return infoList[selectedPosition]
        }
        return null
    }

    // Holder class
    inner class InfoHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var textInfo: TextView?

        init {
            textInfo = view.findViewById(R.id.row)
        }

        fun setInfoData(info: Any) {
            textInfo?.text = info.toString()
            textInfo?.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = adapterPosition
                }
            }
        }

    }
}