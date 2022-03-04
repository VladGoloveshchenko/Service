package com.example.service.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
class LocationService(context: Context) {

    private val locationManager = requireNotNull(context.getSystemService<LocationManager>())

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    val isLocationTrackingEnabled: Boolean
        get() = LocationManagerCompat.isLocationEnabled(locationManager)

    val locationFlow: Flow<Location> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                trySend(locationResult.lastLocation)
            }
        }

        val request = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            locationClient.removeLocationUpdates(callback)
        }
    }

    suspend fun getLocation(): Location? = suspendCoroutine { cont ->
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                cont.resume(location)
            }
            .addOnCanceledListener {
                cont.resume(null)
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 5000L
    }
}