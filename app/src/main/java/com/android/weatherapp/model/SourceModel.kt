package com.android.weatherapp.model

import com.google.gson.annotations.SerializedName

data class SourceModel(
    @SerializedName("title") val title : String,
    @SerializedName("slug") val slug : String,
    @SerializedName("url") val url : String,
    @SerializedName("crawl_rate") val crawl_rate : Int
)