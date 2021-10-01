package com.android.weatherapp.service

import android.widget.Toast
import com.android.weatherapp.WeatherApp
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.system.exitProcess

class NetworkHelper {
    companion object {

        suspend fun <T> safeApiCall(
            dispatcher: CoroutineDispatcher,
            apiCall: suspend () -> T
        ): ResultWrapper<T> {
            return withContext(dispatcher) {
                try {
                    ResultWrapper.Success(apiCall.invoke())
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is IOException -> ResultWrapper.NetworkError
                        is HttpException -> {
                            val code = throwable.code()
                            ResultWrapper.GenericError(code, null)
                        }
                        else -> {
                            ResultWrapper.GenericError(null, null)
                        }
                    }
                }
            }
        }

        private fun convertErrorBody(throwable: HttpException): String? {
            return when (throwable.code()) {
                403 -> "Forbidden"
                404 -> "Not found"
                else -> "Unknown error"
            }

        }
    }
}