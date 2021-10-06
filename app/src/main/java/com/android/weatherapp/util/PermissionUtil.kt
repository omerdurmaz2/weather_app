package com.android.weatherapp.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.android.weatherapp.util.ext.lifecycleOwner
import com.android.weatherapp.util.ext.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PermissionUtil(private val activity: FragmentActivity, private val context: Context) {


    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val requestResult = SingleLiveEvent<Boolean>()
    private var declineCounter = 0

    fun checkPermissions(callback: (Boolean) -> Unit) {
        var counter = 0
        for (element in permissions) {
            ActivityCompat.checkSelfPermission(
                context,
                element
            ).let {
                if (it == PackageManager.PERMISSION_GRANTED) {
                    counter++
                }

            }
        }
        if (counter == permissions.size)
            callback(true)
        else {
            requestPermissions.launch(permissions)
            context.lifecycleOwner()?.let { owner ->
                requestResult.observe(owner) {
                    callback(it)
                    requestResult.removeObservers(owner)
                }
            }
        }
    }


    private val requestPermissions =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.none { it.value == false }) {
                requestResult.postValue(true)
            } else {
                declineCounter++
                if (declineCounter == 2) {
                    showPermissionDialog()
                    declineCounter = 0
                } else {
                    activity.showToast("Yakınınızdaki konumları bulabilmemiz için gerekli izinleri vermeniz gerekiyor.")
                }
                requestResult.postValue(false)
            }

        }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(context).setTitle("İzin gerekli")
            .setMessage("Yakınınızdaki konumları bulabilmemiz için gerekli izinleri vermeniz verin")
            .setPositiveButton("Tamam") { _, _ ->

                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }.setNegativeButton("İptal") { _, _ ->

            }.show()
    }
}