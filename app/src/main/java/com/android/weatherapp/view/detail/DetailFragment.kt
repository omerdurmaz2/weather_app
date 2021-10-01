package com.android.weatherapp.view.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.weatherapp.R
import com.android.weatherapp.base.BaseFragment
import com.android.weatherapp.databinding.DetailFragmentBinding
import com.android.weatherapp.model.LocationWeatherModel
import com.android.weatherapp.model.SearchResultModel
import com.android.weatherapp.service.ResultWrapper
import com.android.weatherapp.util.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : BaseFragment<DetailFragmentBinding>(R.layout.detail_fragment) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DetailFragmentBinding
        get() = DetailFragmentBinding::inflate


    private val viewModel: DetailViewModel by viewModels()

    override fun init() {
        binding?.viewModel = viewModel

        arguments?.getString("location_id").let {
            if (it != null) {
                viewModel.getLocationWeather(it)
            } else activity?.onBackPressed()
        }

        viewModel.locationDetail.observe(viewLifecycleOwner) {
            Log.e("sss", "result: $it")
            when (it) {
                is ResultWrapper.Success<*> -> {
                    binding?.tvSelectedLocation?.text =
                        (it.value as LocationWeatherModel).toString()
                }
                is ResultWrapper.GenericError -> {

                }
                is ResultWrapper.NetworkError -> {
                    showToast(getString(R.string.internet_connection_warning))
                    activity?.onBackPressed()
                }
            }
        }
    }
}