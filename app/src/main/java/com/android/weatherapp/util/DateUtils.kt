package com.android.weatherapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val appDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    private val yearFormat = SimpleDateFormat("yyyy", Locale.US)
    private val dayFormat = SimpleDateFormat("EEEE", Locale.US)
    private val monthFormat = SimpleDateFormat("MMM", Locale.US)

    fun convertApiDateToAppDate(apiDate: String?): String {
        apiDate?.let {
            val newDate = apiDateFormat.parse(apiDate)
            newDate?.let {
                return appDateFormat.format(newDate)
            }
        }
        return "N/A"
    }

    fun convertApiDateToYear(apiDate: String?): String {
        apiDate?.let {
            val newDate = apiDateFormat.parse(apiDate)
            newDate?.let {
                return yearFormat.format(newDate)
            }
        }
        return "N/A"
    }


    fun getLongDateFromApiDate(date: String): Long {
        return apiDateFormat.parse(date).time
    }


    fun getDayName(date: Long): String{
        return dayFormat.format(date)
    }

    fun getMonthName(date: Long): String{
        return monthFormat.format(date)
    }
}