package com.example.mariainesaranguren.parkit

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Tasks
import com.parse.Parse

import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        private const val findParkingZoom: Float = 15.0f
        private val notreDamePosition: LatLng = LatLng(41.7056, -86.2353)
        private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 1
    }
    // Zoom levels
    //    * 10: City
    //    * 15: Streets
    //    * 20: Buildings


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Parse.initialize(this)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get last known location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check location permissions
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("MapsActivity", "Doesn't have permission")
            Log.d("MapsActivity", "Requesting fine location permission")

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)

        } else {
            // Permission has already been granted
            setup_map()
        }
    }

    /**
     * Callback when permissions are requested and user takes action
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    setup_map()

                } else {
                    // Permission denied
                    val toast = Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT)
                    toast.show()
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    /**
     * Function to determine the user's location, move the map appropriately, and find parking around him
     */
    fun setup_map() = async {
        // Get the user's location
        var location: Location? = getLocation().await()

        if(location == null) {
            Log.d("MainActivity", "Location is null")
            val toast = Toast.makeText(this@MapsActivity, "Unable to get current location", Toast.LENGTH_SHORT)
            toast.show()
            return@async
        }

        // Move camera to current location and place marker
        Log.d("MainActivity", "Moving camera to user's location")
        setCamera(LatLng(location.latitude, location.longitude), findParkingZoom, "Current Location")

        // TODO: find parking around current user
        Log.d("MapsActivity", "Reached \"find parking around current user\"")
    }

    /**
     * Function to get current user's location
     * Returns null if the location couldn't be established
     */
    fun getLocation() = async<Location?> {
        // Get location of user
        Log.d("MapsActivity", "Attempting to show current location")
        var location: Location?
        try {
            Log.d("MapsActivity", "Getting location....")
            location = async {
                //return@async Tasks.await(fusedLocationClient.lastLocation)
                return@async Location("Test")
            }.await()
            Log.d("MapsActivity", "Returned from awaited task to retrieve location")
        }
        catch(e: SecurityException) {
            Log.d("MapsActivity", "Failed to retrieve current location due to SecurityException")
            Log.e("MainActivity", "SecurityException when trying to retrieve location", e)
            return@async null
        }

        // Return the location, if obtained correctly
        if (location != null) {
            Log.d("MapsActivity", "Successfully retrieved current location")
            return@async location
        }

        //Otherwise, return null
        Log.d("MapsActivity", "Failed to retrieve current location")
        return@async null
    }

    /**
     * Function to move the camera to a given location and zoom to the given amount
     */
    fun setCamera(location: LatLng, zoom: Float, label: String) {
        Log.d("MapsActivity", "Marking location at (" + location.latitude + ", " + location.longitude + ")")
        mMap.addMarker(MarkerOptions().position(location).title(label))
        Log.d("MainActivity", "Moving camera to location")
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }

}

