package com.android.weatherapp.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.weatherapp.R
import com.android.weatherapp.base.BaseFragment
import com.android.weatherapp.databinding.DetailFragmentBinding
import com.android.weatherapp.model.LocationWeatherModel
import com.android.weatherapp.service.ResultWrapper
import com.android.weatherapp.util.Constants
import com.android.weatherapp.util.DateUtils
import com.android.weatherapp.util.ext.setGone
import com.android.weatherapp.util.ext.setVisible
import com.android.weatherapp.util.ext.showToast
import com.android.weatherapp.util.ext.tempFormat
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailFragment : BaseFragment<DetailFragmentBinding>(R.layout.detail_fragment) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DetailFragmentBinding
        get() = DetailFragmentBinding::inflate


    private val viewModel: DetailViewModel by viewModels()
    private lateinit var listAdapter: FutureWeathersAdapter
    override fun init() {
        binding?.viewModel = viewModel

        arguments?.getString("location_id").let {
            if (it != null) {
                viewModel.getLocationWeather(it)
            } else activity?.onBackPressed()
        }

        initRecyclerView()

        observeDetail()
    }

    private fun initRecyclerView() {
        listAdapter = FutureWeathersAdapter(context, listOf()) {}
        binding?.rvDetailFragment?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    private fun observeDetail() {
        viewModel.locationDetail.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Success<*> -> {
                    bindValues(it.value as LocationWeatherModel)
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


    private fun bindValues(value: LocationWeatherModel) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis =
            DateUtils.getLongDateFromApiDate(value.consolidatedWeather[0].applicable_date)


        binding?.pbDetail?.setGone()
        binding?.vDetailTitleLine?.setVisible()
        Constants.Server.apply {
            context?.let {
                binding?.ivDetailStatus?.let { it1 ->
                    Glide.with(it).load(
                        baseUrl.plus(imagePath)
                            .plus(value.consolidatedWeather[0].weather_state_abbr).plus(
                                fileType
                            )
                    ).into(it1)
                }
            }
        }


        binding?.tvDetailLocationName?.text = value.title
        binding?.tvDetailDate?.text =
            DateUtils.getDayName(calendar.timeInMillis).plus(", ")
                .plus(DateUtils.get3LetterMonthName(calendar.timeInMillis)).plus(" ")
                .plus(calendar.get(Calendar.DAY_OF_MONTH))
        binding?.tvDetailTemp?.text = value.consolidatedWeather[0].the_temp.tempFormat()


        listAdapter.list = value.consolidatedWeather
        listAdapter.notifyItemInserted(0)

    }
}