package com.android.weatherapp.service

import com.android.weatherapp.WeatherApp
import com.android.weatherapp.service.factories.WeatherFactory
import com.android.weatherapp.util.Constants
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class RestControllerFactory() {

    private var weatherFactory: WeatherFactory

    init {

        val chuckerCollector = ChuckerCollector(
            context = WeatherApp.getApplicationContext(),
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
        val chuckerInterceptor = ChuckerInterceptor.Builder(WeatherApp.getApplicationContext())
            .collector(chuckerCollector)
            .alwaysReadResponseBody(true)
            .build()

        val okHttpClient = OkHttpClient.Builder().addInterceptor(chuckerInterceptor)
            .connectTimeout(timeoutInterval, TimeUnit.SECONDS)
            .readTimeout(timeoutInterval, TimeUnit.SECONDS)

        client = okHttpClient.build()

        val apiService: Retrofit = Retrofit.Builder().baseUrl(Constants.Server.baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()

        weatherFactory = apiService.create(WeatherFactory::class.java)
    }

    companion object {
        const val timeoutInterval = 300L
        var client = OkHttpClient()

    }

    fun getWeatherFactory(): WeatherFactory {
        return weatherFactory
    }
}