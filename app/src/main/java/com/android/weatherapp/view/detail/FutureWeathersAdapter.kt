package com.android.weatherapp.view.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.weatherapp.R
import com.android.weatherapp.model.WeatherItemModel
import com.android.weatherapp.util.DateUtils
import java.util.*

class FutureWeathersAdapter(
    val context: Context?,
    var list: List<WeatherItemModel?>,
    val clickListener: (WeatherItemModel?) -> Unit
) :
    RecyclerView.Adapter<FutureWeathersAdapter.ItemViewHolder>() {


    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvItemFutureDayName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_future_day_weather, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position == 0) return
        val item = list[position]
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = DateUtils.getLongDateFromApiDate(item?.applicable_date ?: "")
        holder.title.text = DateUtils.getDayName(calendar.timeInMillis)
        holder.itemView.setOnClickListener {
            clickListener(item)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}


