package com.android.weatherapp.service.factories

import com.android.weatherapp.model.LocationWeatherModel
import com.android.weatherapp.model.SearchResultModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherFactory {


    @GET("api/location/search/")
    suspend fun searchLocationByName(
        @Query("query") searchText: String,
    ): List<SearchResultModel>

    @GET("api/location/search/")
    suspend fun searchLocationByLattLong(
        @Query("lattlong") lattLong: String,
    ): List<SearchResultModel>

    @GET("api/location/{woeid}/")
    suspend fun getLocationWeather(
        @Path("woeid") woeid: String,
    ): LocationWeatherModel

}