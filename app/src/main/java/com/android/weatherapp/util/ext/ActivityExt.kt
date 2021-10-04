package com.android.weatherapp.util.ext

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController


fun Activity.showToast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(this, message, duration).show()
    }
}


fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
