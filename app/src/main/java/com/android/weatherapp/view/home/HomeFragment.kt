package com.android.weatherapp.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
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
import com.android.weatherapp.view.MainActivity
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import com.android.weatherapp.util.ext.*


@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>(R.layout.home_fragment) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> HomeFragmentBinding
        get() = HomeFragmentBinding::inflate


    private val viewModel: HomeViewModel by viewModels()
    private var locationClient: FusedLocationProviderClient? = null
    private var listAdapter: SearchResultAdapter? = null
    var permissionUtil: PermissionUtil? = null


    override fun init() {
        binding?.viewModel = viewModel

        if (permissionUtil == null)
            permissionUtil = activity?.let { context?.let { it1 -> PermissionUtil(it, it1) } }

        observeLocations()

        if (locationClient == null)
            locationClient =
                LocationServices.getFusedLocationProviderClient((activity as MainActivity));

        initRecyclerView()

        if (viewModel.searchText == "" && viewModel.latitude == 0.0 && viewModel.longitude == 0.0) {
            getDeviceLocation()
        }

        searchImeOptionListener()
    }


    private fun initRecyclerView() {
        if (listAdapter != null) return
        listAdapter = SearchResultAdapter(context, listOf()) {
            hideSoftKeyboard()
            val bundle = Bundle()
            bundle.putString("location_id", it?.woeid.toString())
            navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding?.rvHome?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }


    override fun initTextChangeListeners() {
        super.initTextChangeListeners()

        binding?.etSearchLocation?.doAfterTextChanged {
            if (it.toString().trim() != "" && it.toString().trim() != viewModel.searchText)
                binding?.pbHome?.setVisible()
            else binding?.pbHome?.setGone()


            viewModel.startCountDown(it.toString().trim())
        }
    }

    private fun searchImeOptionListener() {
        binding?.tilSearchLocation?.editText?.setOnEditorActionListener { textView, i, keyEvent ->

            if (i == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.startCountDown(binding?.etSearchLocation?.text.toString().trim())
                hideSoftKeyboard()
                true
            }
            false
        }
    }


    override fun initClickListeners() {
        super.initClickListeners()
        binding?.ivFindMyLocation?.setOnClickListener {
            binding?.etSearchLocation?.text?.clear()
            viewModel.searchText = ""
            getDeviceLocation()
            hideSoftKeyboard()
        }
    }

    private fun observeLocations() {
        viewModel.searchLocations.removeObservers(viewLifecycleOwner)

        viewModel.searchLocations.observe(viewLifecycleOwner) {
            binding?.pbHome?.setGone()
            when (it) {
                is ResultWrapper.Success<*> -> {
                    var list = it.value as List<SearchResultModel?>
                    listAdapter?.list = list
                    listAdapter?.notifyDataSetChanged()
                }
                is ResultWrapper.GenericError -> {

                }
                is ResultWrapper.NetworkError -> {
                    showToast(getString(R.string.internet_connection_warning))
                    activity?.onBackPressed()
                }
                null -> {

                }
            }
        }
    }

    private fun getDeviceLocation() {
        permissionUtil?.checkPermissions {
            if (it) {
                if (isLocationEnabled()) {
                    getDeviceLastLocation()
                } else {
                    navigateToLocationSettings()
                }
            } else {
                binding?.etSearchLocation?.requestFocus()
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.showSoftInput(binding?.etSearchLocation, InputMethodManager.SHOW_IMPLICIT)
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

        locationClient?.lastLocation?.addOnCompleteListener {
            if (it.result != null) {
                viewModel.latitude = it.result.latitude
                viewModel.longitude = it.result.longitude
                viewModel.getLocationsByLattLong()
                binding?.pbHome?.setVisible()
            } else {
                requestNewLocation()
            }
        }


    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        locationClient = LocationServices.getFusedLocationProviderClient(context)
        locationClient?.requestLocationUpdates(
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
            binding?.pbHome?.setVisible()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager? =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true || locationManager?.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ) == true
    }

}