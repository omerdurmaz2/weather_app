package com.android.weatherapp.util.ext

import android.view.View
import androidx.navigation.NavController


fun Double.tempFormat(): String {
    return String.format("%.0f", this).plus("°")
}