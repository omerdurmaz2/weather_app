package com.android.weatherapp.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.weatherapp.model.SearchResultModel
import com.android.weatherapp.service.NetworkHelper
import com.android.weatherapp.service.RestControllerFactory
import com.android.weatherapp.service.ResultWrapper
import com.android.weatherapp.util.SingleLiveEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restController: RestControllerFactory,
    val gson: Gson,
) : ViewModel() {

    var latitude = 0.0
    var longitude = 0.0
    private var _searchLocations =
        SingleLiveEvent<ResultWrapper<List<SearchResultModel>>?>().apply { value = null }
    val searchLocations: LiveData<ResultWrapper<List<SearchResultModel>>?> get() = _searchLocations
    var searchText = ""
    var editCounter = 0

    fun getLocationsByLattLong() {


        viewModelScope.launch {
            val lattLong = "$latitude, $longitude"

            _searchLocations.postValue(NetworkHelper.safeApiCall(Dispatchers.IO) {
                restController.getWeatherFactory().searchLocationByLattLong(lattLong)
            })
        }

    }


    fun startCountDown(text: String) {
        if (searchText == text) return
        editCounter++
        Log.e("sss", "start countdown $editCounter")
        searchText = text
        viewModelScope.launch {
            delay(500)
            editCounter--
            Log.e("sss", "start countdown $editCounter")

            if (searchText.isNotEmpty() && searchText.length >= 3 && editCounter == 0) {
                Log.e("sss", "search text")
                getLocationsByQueryText()
            }
        }
    }

    private fun getLocationsByQueryText() {
        viewModelScope.launch {
            _searchLocations.postValue(NetworkHelper.safeApiCall(Dispatchers.IO) {
                restController.getWeatherFactory().searchLocationByName(searchText)
            })
        }
    }


}