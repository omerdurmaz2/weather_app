package com.android.weatherapp.util.ext

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.weatherapp.util.ext.showToast


fun Fragment.showToast(
    message: CharSequence,
    duration: Int = Toast.LENGTH_SHORT
) = activity?.showToast(message, duration)



