package com.example.mariainesaranguren.parkit

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

interface ParkingLocation: Parcelable {

    fun getLocationName(): String // Function to get the name of the parking location

    fun getLocationAddr(): String // Function to get the address of the parking location

    fun getLatLng(): LatLng // Get the latitude and longitude of the parking location

}