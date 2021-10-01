package com.android.weatherapp.util.ext

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController


fun Activity.showToast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(this, message, duration).show()
    }
}