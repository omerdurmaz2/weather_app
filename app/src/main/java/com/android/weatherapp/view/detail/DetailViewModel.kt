package com.android.weatherapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.weatherapp.model.LocationWeatherModel
import com.android.weatherapp.service.NetworkHelper
import com.android.weatherapp.service.RestControllerFactory
import com.android.weatherapp.service.ResultWrapper
import com.android.weatherapp.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val restController: RestControllerFactory,
) : ViewModel() {

    private var _locationDetail = SingleLiveEvent<ResultWrapper<LocationWeatherModel>>()
    val locationDetail: LiveData<ResultWrapper<LocationWeatherModel>> get() = _locationDetail


    fun getLocationWeather(id: String) {
        viewModelScope.launch {
            _locationDetail.postValue(NetworkHelper.safeApiCall(Dispatchers.IO) {
                restController.getWeatherFactory().getLocationWeather(id)
            })
        }

    }
}