# Park-It

An Android application for finding a parking spot. Utilizes hardware (the code for which can be found [here](https://github.com/ryansmick/ParkIt-Pi)) to determine whether or not a parking space is open.

## Development Environment

The code for this application was written in Kotlin using Android Studio. All of the necessary packages are stored in the gradle files included in the app.

## Important Files

All of the developer-created files are stored in /app/src/main/java/com/example/mariainesaranguren/parkit.

- **App.kt**: Entrypoint for the app. Sets up the Parse SDK.  
- **MapsActivity.kt**: Our main activity. Displays the map and utilizes the ParkingResultsFragment and StartNavigationFragment to allow the user to navigate to various locations.  
- **MyParkingResultsRecyclerViewAdapter.kt**: Controls ParkingResultsFragment in the background. Stores list of parking results.  
- **OtherLocation.kt**: Class implementing ParkingLocation that represents a location to which the user might travel that is not stored in our backend.  
- **ParkingLocation.kt**: Interface describing a location to which the user might travel.  
- **ParkingLot.kt**: Class implementing ParkingLocation that represents a parking lot to which the user might travel.  
- **ParkingResultsFragment.kt**: Fragment to display a list of nearby parking locations.  
- **StartNavigationFragment.kt**: Fragment to display a confirmation dialog where the user can begin navigation to a location.  

## A Note on API Keys

The API keys needed to run the app are not included in this source code. If you would like to use the application, you may add a file at /app/src/main/res/values/keys.xml and include the following code:

```
<resources>
    <string name="google_maps_key">INSERT_KEY_HERE</string>
    <string name="back4app_app_id">INSERT_APP_ID_HERE</string>
    <string name="back4app_client_key">INSERT_KEY_HERE</string>
    <string name="back4app_server_url">https://parseapi.back4app.com/</string>
</resources>
```

Additionally, you may also ask one of the collaborators for their API keys and add them at the same location.
