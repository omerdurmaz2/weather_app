package com.android.weatherapp.model

import com.google.gson.annotations.SerializedName

data class LocationWeatherModel(
    @SerializedName("consolidated_weather") val consolidatedWeather : List<WeatherItemModel>,
    @SerializedName("time") val time : String,
    @SerializedName("sun_rise") val sunRise : String,
    @SerializedName("sun_set") val sunSet : String,
    @SerializedName("timezone_name") val timezoneName : String,
    @SerializedName("parent") val parent : SearchResultModel,
    @SerializedName("sources") val sources : List<SourceModel>,
    @SerializedName("title") val title : String,
    @SerializedName("location_type") val locationType : String,
    @SerializedName("woeid") val woeid : Int,
    @SerializedName("latt_long") val latt_long : String,
    @SerializedName("timezone") val timezone : String
)