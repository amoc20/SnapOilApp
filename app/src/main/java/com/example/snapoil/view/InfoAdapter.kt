package com.example.snapoil.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapoil.R

class InfoAdapter(private val infoList: List<Any>) : RecyclerView.Adapter<InfoAdapter.InfoHolder>() {
    private var onInfoClickListener: OnInfoClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoHolder {
        return InfoHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.info_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InfoHolder, position: Int) {
        val info = infoList[position]
        holder.setInfoData(info)

        holder.itemView.setOnClickListener {
            if (onInfoClickListener != null) {
                onInfoClickListener!!.onInfoClick(position, info)
            }
        }
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    fun setOnInfoClickListener(onInfoClickListener: OnInfoClickListener) {
        this.onInfoClickListener = onInfoClickListener
    }

    interface OnInfoClickListener {
        fun onInfoClick(position: Int, info: Any)
    }

    // Holder class
    inner class InfoHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var textInfo: TextView?

        init {
            textInfo = view.findViewById(R.id.row)
        }

        fun setInfoData(info: Any) {
            textInfo?.text = info.toString()
        }
    }
}