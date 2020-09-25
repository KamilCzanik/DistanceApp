package xyz.czanik.distanceapp

import android.content.Context
import android.net.ConnectivityManager

fun hasNetwork(context: Context): Boolean = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
        ?.activeNetworkInfo
        ?.isConnected == true