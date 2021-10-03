package com.android.weatherapp.util.ext

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.android.weatherapp.util.ext.showToast


fun Fragment.showToast(
    message: CharSequence,
    duration: Int = Toast.LENGTH_SHORT
) = activity?.showToast(message, duration)


fun Fragment.showSoftKeyboard() {
    val inputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}