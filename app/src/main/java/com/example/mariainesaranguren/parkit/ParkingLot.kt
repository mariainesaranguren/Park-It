package com.example.mariainesaranguren.parkit

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

class ParkingLot private constructor() : ParkingLocation, Parcelable {

    private lateinit var name: String
    private lateinit var address: String
    private lateinit var location: LatLng

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        address = parcel.readString()
        location = parcel.readParcelable(LatLng::class.java.classLoader)
    }

    constructor(name: String, address: String, location: LatLng) : this() {
        this.name = name
        this.address = address
        this.location = location
    }

    override fun getLocationName(): String {
        return this.name
    }

    override fun getLocationAddr(): String {
        return this.address
    }

    override fun getLatLng(): LatLng {
        return this.location
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeParcelable(location, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParkingLot> {
        override fun createFromParcel(parcel: Parcel): ParkingLot {
            return ParkingLot(parcel)
        }

        override fun newArray(size: Int): Array<ParkingLot?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "{name: ${name}, address: ${address}, LatLng: ${location.toString()}}"
    }


}