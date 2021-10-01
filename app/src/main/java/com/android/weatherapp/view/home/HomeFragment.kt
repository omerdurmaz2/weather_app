package com.android.weatherapp.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.weatherapp.R
import com.android.weatherapp.base.BaseFragment
import com.android.weatherapp.databinding.HomeFragmentBinding
import com.android.weatherapp.model.SearchResultModel
import com.android.weatherapp.service.ResultWrapper
import com.android.weatherapp.util.PermissionUtil
import com.android.weatherapp.util.ext.showToast
import com.android.weatherapp.view.MainActivity
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>(R.layout.home_fragment) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> HomeFragmentBinding
        get() = HomeFragmentBinding::inflate


    private val viewModel: HomeViewModel by viewModels()
    private var permissionUtil: PermissionUtil? = null
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var listAdapter: SearchResultAdapter

    override fun init() {
        binding?.viewModel = viewModel

        permissionUtil = activity?.let { context?.let { it1 -> PermissionUtil(it, it1) } }

        observeLocations()

        locationClient =
            LocationServices.getFusedLocationProviderClient((activity as MainActivity));

        initRecyclerView()

        getDeviceLocation()

        onTextChange()
    }

    private fun initRecyclerView() {
        listAdapter = SearchResultAdapter(context, listOf()) {
            val bundle = Bundle()
            bundle.putString("location_id", it?.woeid.toString())
            navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding?.rvHome?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    private fun onTextChange() {
        binding?.etSearchLocation?.doAfterTextChanged {
            viewModel.editCounter++
            viewModel.startCountDown(it.toString().trim())
        }
    }


    override fun initClickListeners() {
        super.initClickListeners()
        binding?.ivFindMyLocation?.setOnClickListener {
            getDeviceLocation()
        }
    }

    private fun observeLocations() {
        viewModel.searchLocations.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Success<*> -> {
                    var list = it.value as List<SearchResultModel?>
                    Log.e("sss", "list$list")
                    listAdapter.list = list
                    listAdapter.notifyDataSetChanged()
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

    private fun getDeviceLocation() {
        Log.e("sss", "get device location")
        permissionUtil?.checkPermissions {
            if (it) {
                if (isLocationEnabled()) {
                    getDeviceLastLocation()
                } else {
                    navigateToLocationSettings()
                }
            } else {
                binding?.etSearchLocation?.requestFocus()
            }
        }
    }


    private fun navigateToLocationSettings() {
        showToast("Please turn on" + " your location...")
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLastLocation() {
        Log.e("sss", "get last location")

        locationClient.lastLocation.addOnCompleteListener {
            if (it.result != null) {
                viewModel.latitude = it.result.latitude
                viewModel.longitude = it.result.longitude
                viewModel.getLocationsByLattLong()
            } else {
                requestNewLocation()
            }
        }


    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        Log.e("sss", "request new location")

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        locationClient = LocationServices.getFusedLocationProviderClient(context)
        locationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            viewModel.latitude = p0.lastLocation.latitude
            viewModel.longitude = p0.lastLocation.longitude
            viewModel.getLocationsByLattLong()
        }
    }


    private fun isLocationEnabled(): Boolean {

        Log.e("sss", "is location enabled")
        val locationManager: LocationManager? =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true || locationManager?.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ) == true
    }

}