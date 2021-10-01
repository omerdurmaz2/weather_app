package com.android.weatherapp.model

import com.google.gson.annotations.SerializedName

data class SearchResultModel(
    @SerializedName("distance") val distance: Int?,
    @SerializedName("title") val title: String,
    @SerializedName("location_type") val LocationType: String,
    @SerializedName("woeid") val woeid: Int,
    @SerializedName("latt_long") val lattLong: String,
)