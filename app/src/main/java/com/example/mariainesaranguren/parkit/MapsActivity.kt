package com.example.mariainesaranguren.parkit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.support.v4.app.FragmentTransaction
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.parse.*
import com.parse.ParseObject
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseGeoPoint
import com.parse.ParseException
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, StartNavigationFragment.OnFragmentInteractionListener, GoogleMap.OnMarkerClickListener, ParkingResultsFragment.OnListFragmentInteractionListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    companion object {
        private const val findParkingZoom: Float = 15.0f
        private val notreDamePosition: LatLng = LatLng(41.7056, -86.2353)
        private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 1
        private val queryResultsMarkers:HashMap<Marker, ParkingLocation> = HashMap()
        private val queryResultsParkingLocation: ArrayList<ParkingLocation> = ArrayList()
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    }
    // Zoom levels
    //    * 10: City
    //    * 15: Streets
    //    * 20: Buildings

    override fun onListFragmentIteraction(item: ParkingLocation) {
        Log.i("MapsActivity", "onListFragmentInteraction, on item: "+item.getLocationName())

        // Move camera to selected destination
        setCamera(item.getLatLng(), findParkingZoom)

        // Allow user to start trip
        prepareForNavigation(item)
    }

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.search_button) {
            // Start the search activity
            Log.i("MapsActivity", "Starting LocationSearchActivity...")
            //val intent = Intent(this, LocationSearchActivity::class.java)
            //startActivity(intent)
            findPlace()

        }
        return super.onOptionsItemSelected(item)
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
        showLocation()
        mMap.setOnMarkerClickListener(this)
    }

    fun showLocation(){
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("showLocation", "Doesn't have permission")
            Log.d("showLocation", "Going to request permission")

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)

        } else {
            // Permission has already been granted
            var position: LatLng = notreDamePosition
            Log.d("showLocation", "Position set to ND")

            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            position = LatLng(location.getLatitude(), location.getLongitude())
                            currentLocation = position

                            // Add on click listener for fab
                            fab.setOnClickListener {
                                setCamera(currentLocation, findParkingZoom)
                            }

                            Log.d("showLocation", "Position set to current location")
                            Log.v("showLocation", position.toString())
                            mMap.addMarker(MarkerOptions().position(currentLocation).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            setCamera(position, findParkingZoom)
                            Log.d("showLocation", "current location "+position.latitude.toString()+" "+position.longitude.toString())


                            // Query for nearby parking spots
                            Log.d("showLocation", "preparing query")
                            val currLocation = ParseGeoPoint(position.latitude, position.longitude)     // Creating a GeoPoint with the desired point
                            val query = ParseQuery.getQuery<ParseObject>("parking_lot")      // Creating a Query so that it can search in the DB
                            query.whereNear("position", currLocation)                             // Query locations near that point within the field named parking_lot
                            query.limit = 8                                                            // Setting a query limit, to avoid an excess of results
                            Log.d("showLocation", "going to query now")                     // Then start the query with a Callback
                            query.findInBackground { objects, e ->
                                // Clear results map
                                queryResultsMarkers.clear()
                                Log.d("showLocation", "done querying")
                                Log.d("showLocation", "error: ", e)
                                // Add markers for each query result
                                for (i in objects.indices) {
                                    val queryLatitude = objects[i].getParseGeoPoint("position").latitude
                                    val queryLongitude = objects[i].getParseGeoPoint("position").longitude
                                    val queryTitle = objects[i].getString("name")
                                    val queryAddr = objects[i].getString("address")
                                    Log.d("showLocation()", "query Lat"+queryLatitude.toString()+" query Long"+queryLongitude.toString())
                                    var parking_spot = LatLng(queryLatitude, queryLongitude)
                                    val marker = mMap.addMarker(MarkerOptions().position(parking_spot).title(queryTitle))

                                    val newParkingLocation = ParkingLot(queryTitle, queryAddr, parking_spot)
                                    queryResultsMarkers.put(marker, newParkingLocation)
                                    queryResultsParkingLocation.add(newParkingLocation)
                                }
                                showParkingSpotsList()
                            }
                        }
                    }
                    .addOnFailureListener {
                        val toast = Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT)
                        toast.show()
                    }
        }
    }

    override fun cancelNavigation() {
        showParkingSpotsList()
    }

    fun setCamera(location: LatLng, zoom: Float) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                var position: LatLng = notreDamePosition
                Log.d("onRequestPer", "Position set to ND")
                Log.d("onRequestPer", "Permission granted")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    // Get most recent location and set camera
                    fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    currentLocation = LatLng(location.getLatitude(), location.getLongitude())

                                    // Add on click listener for fab
                                    fab.setOnClickListener {
                                        setCamera(currentLocation, findParkingZoom)
                                    }

                                    Log.v("onRequestPer", currentLocation.toString())
                                    mMap.addMarker(MarkerOptions().position(currentLocation).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                                    setCamera(currentLocation, findParkingZoom)
                                }
                                Log.d("onRequestPer", "Position set to current location")

                                // TODO Add code here
                            }
                            .addOnFailureListener {
                                val toast = Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT)
                                toast.show()
                            }
                } else {
                    // Permission denied
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun showParkingSpotsList() {
        Log.i("MapsActivity", "showParkingSpotsList")
        top_frame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                2.0f
        )

        bottom_fragment.setVisibility(View.VISIBLE);
        bottom_fragment.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        )

        val fragment = ParkingResultsFragment.newInstance(1, queryResultsParkingLocation)
        getSupportFragmentManager().beginTransaction()
                .replace(bottom_fragment.id, fragment, "StartNav").commit();
    }

    // Function called when a map marker is clicked
    override fun onMarkerClick(marker: Marker?): Boolean {
        val location: ParkingLocation? = queryResultsMarkers.get(marker)
        if(marker != null && location != null) {
            prepareForNavigation(location)
            return true
        }

        // Returning false indicates that this handler is not meant to handle this click
        return false
    }

    // Function called to have the map show the navigation route
    override fun prepareForNavigation(location: ParkingLocation) {
        // Start StartnavigationFragment
        Log.i("MapsActivity", "Starting StartNavigationFragment. location={${location.toString()}}")

        top_frame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        )

        bottom_fragment.setVisibility(View.VISIBLE);
        bottom_fragment.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.0f
        )

        val fragment = StartNavigationFragment.newInstance(location)
        getSupportFragmentManager().beginTransaction()
                .replace(bottom_fragment.id, fragment, "StartNav").commit();


    }

    //Function called to have the map begin navigation
    override fun beginNavigation(location: ParkingLocation) {
        Log.i("MapsActivity", "Beginning navigation to location={${location.toString()}}")

        // Build directions URL so we can launch Google Maps
        var url: String = "https://www.google.com/maps/dir/?api=1&origin=${currentLocation.latitude},${currentLocation.longitude}&destination=${location.getLatLng().latitude},${location.getLatLng().longitude}"

        // Start intent
        var intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            val toast = Toast.makeText(this, "Unable to open directions. No appropriate apps installed", Toast.LENGTH_SHORT)
            toast.show()
            Log.e("MapsActivity", "Failed to show directions. No apps installed.")
        }

    }

    fun findPlace() {
        try {
            val intent: Intent =
                    PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
        }
    }

    // A place has been received; use requestCode to track the request.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                var place: Place = PlaceAutocomplete.getPlace(this, data)
                Log.i("MapsActivity", "Selected place: " + place.getName());
                var resultLocation: OtherLocation = OtherLocation(place.name.toString(), place.address.toString(), place.latLng)
                setCamera(resultLocation.getLatLng(), findParkingZoom)
                prepareForNavigation(resultLocation)
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                var status: Status = PlaceAutocomplete.getStatus(this, data)
                // TODO: Handle the error.
                Log.e("MapsActivity", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}

