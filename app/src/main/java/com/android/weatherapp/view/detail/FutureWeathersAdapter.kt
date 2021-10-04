package com.android.weatherapp.view.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.weatherapp.R
import com.android.weatherapp.model.WeatherItemModel
import com.android.weatherapp.util.Constants
import com.android.weatherapp.util.DateUtils
import com.android.weatherapp.util.ext.setGone
import com.android.weatherapp.util.ext.tempFormat
import com.bumptech.glide.Glide
import java.util.*

class FutureWeathersAdapter(
    val context: Context?,
    var list: List<WeatherItemModel?>,
    val clickListener: (WeatherItemModel?) -> Unit
) :
    RecyclerView.Adapter<FutureWeathersAdapter.ItemViewHolder>() {


    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvItemFutureDayName)
        val temp: TextView = view.findViewById(R.id.tvItemFutureDayTemp)
        val icon: ImageView = view.findViewById(R.id.ivItemFutureDayIcon)
        val separator: View = view.findViewById(R.id.vItemFutureDayWeatherSeparator)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_future_day_weather, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position + 1]
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = DateUtils.getLongDateFromApiDate(item?.applicable_date ?: "")
        holder.title.text = DateUtils.get3LetterDayName(calendar.timeInMillis).plus(", ")
            .plus(DateUtils.get3LetterMonthName(calendar.timeInMillis)).plus(" ")
            .plus(calendar.get(Calendar.DAY_OF_MONTH))

        Constants.Server.apply {
            context?.let {
                Glide.with(it).load(
                    baseUrl.plus(imagePath)
                        .plus(item?.weather_state_abbr).plus(
                            fileType
                        )
                ).into(holder.icon)
            }
        }

        holder.temp.text = item?.the_temp?.tempFormat()

        if (position + 2 == list.size) holder.separator.setGone()

        holder.itemView.setOnClickListener {
            clickListener(item)
        }

    }

    override fun getItemCount(): Int {
        return list.size - 1
    }

}


