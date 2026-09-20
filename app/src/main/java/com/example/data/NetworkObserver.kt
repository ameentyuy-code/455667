package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.model.NetworkStatus

@Composable
fun rememberNetworkStatus(): State<NetworkStatus> {
    val context = LocalContext.current
    val status = remember {
        mutableStateOf(getInitialNetworkStatus(context))
    }

    DisposableEffect(context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        if (connectivityManager == null) {
            status.value = NetworkStatus.ONLINE
            return@DisposableEffect onDispose {}
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                status.value = NetworkStatus.ONLINE
            }

            override fun onLost(network: Network) {
                status.value = NetworkStatus.OFFLINE
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                status.value = if (hasInternet) NetworkStatus.ONLINE else NetworkStatus.OFFLINE
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        onDispose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
        }
    }

    return status
}

private fun getInitialNetworkStatus(context: Context): NetworkStatus {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return NetworkStatus.ONLINE
    val activeNetwork = cm.activeNetwork ?: return NetworkStatus.OFFLINE
    val caps = cm.getNetworkCapabilities(activeNetwork) ?: return NetworkStatus.OFFLINE
    return if (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
        NetworkStatus.ONLINE
    } else {
        NetworkStatus.OFFLINE
    }
}
