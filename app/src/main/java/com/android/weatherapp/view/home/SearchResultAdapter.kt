package com.android.weatherapp.view.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.weatherapp.R
import com.android.weatherapp.model.SearchResultModel
import com.android.weatherapp.util.ext.setGone

class SearchResultAdapter(
    val context: Context?,
    var list: List<SearchResultModel?>,
    val clickListener: (SearchResultModel?) -> Unit
) :
    RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvItemLocationCity)
        val seperator: View = view.findViewById(R.id.vItemLocationSeperator)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_location, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]

        holder.title.text = item?.title

        holder.itemView.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


