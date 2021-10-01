package com.android.weatherapp.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.weatherapp.model.LocationWeatherModel
import com.android.weatherapp.model.SearchResultModel
import com.android.weatherapp.service.NetworkHelper
import com.android.weatherapp.service.RestControllerFactory
import com.android.weatherapp.service.ResultWrapper
import com.android.weatherapp.util.SingleLiveEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val restController: RestControllerFactory,
) : ViewModel() {

    var locationDetail = SingleLiveEvent<ResultWrapper<LocationWeatherModel>>()


    fun getLocationWeather(id: String) {
        viewModelScope.launch {
            locationDetail.postValue(NetworkHelper.safeApiCall(Dispatchers.IO) {
                restController.getWeatherFactory().getLocationWeather(id)
            })
        }

    }
}