package com.example.homehealth.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Wraps FusedLocationProvider + Geocoder so callers can ask "what's the user's
 * current zip code?" in one suspend call.
 *
 * Returns null when permission is missing, location can't be fetched, or the
 * geocoder can't resolve a postal code. The caller decides how to fall back
 * (e.g. prompt the user to type a zip manually).
 */
class LocationService(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private fun hasLocationPermission(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /**
     * Fetch the device's current location. Uses getCurrentLocation rather than
     * lastLocation because lastLocation can be null right after install or
     * after the OS has cleared its cache.
     */
    @Suppress("MissingPermission") // checked above via hasLocationPermission()
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null
        return try {
            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Reverse-geocode a lat/lng to a US zip code. Runs the (blocking) Geocoder
     * call on the IO dispatcher.
     */
    suspend fun zipCodeFor(location: Location): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.US)
            @Suppress("DEPRECATION") // the async API is API 33+; this works everywhere
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses?.firstOrNull()?.postalCode
        } catch (e: Exception) {
            null
        }
    }

    /** Convenience: location → zip code in one call. */
    suspend fun getCurrentZipCode(): String? {
        val loc = getCurrentLocation() ?: return null
        return zipCodeFor(loc)
    }
}
