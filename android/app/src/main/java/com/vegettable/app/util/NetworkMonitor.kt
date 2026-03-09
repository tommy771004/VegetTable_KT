package com.vegettable.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * 網路狀態監控 — 使用 ConnectivityManager 偵測離線/上線狀態
 */
class NetworkMonitor private constructor(context: Context) {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _isConnected = MutableLiveData(true)
    val isConnected: LiveData<Boolean> get() = _isConnected

    init {
        registerCallback()
    }

    fun isOnline(): Boolean {
        return _isConnected.value ?: true
    }

    private fun registerCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.postValue(true)
            }

            override fun onLost(network: Network) {
                _isConnected.postValue(false)
            }

            override fun onUnavailable() {
                _isConnected.postValue(false)
            }
        })
    }

    companion object {
        @Volatile
        private var instance: NetworkMonitor? = null

        @JvmStatic
        fun getInstance(context: Context): NetworkMonitor {
            return instance ?: synchronized(this) {
                instance ?: NetworkMonitor(context.applicationContext).also { instance = it }
            }
        }
    }
}
