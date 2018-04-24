package com.example.mariainesaranguren.parkit

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.example.mariainesaranguren.parkit.ParkingResultsFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_parkingresults.view.*

/**
 * [RecyclerView.Adapter] that can display a [ParkingLocation] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyParkingResultsRecyclerViewAdapter(
        private val parkingSpots: ArrayList<ParkingLocation>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyParkingResultsRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ParkingLocation
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentIteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_parkingresults, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val item = parkingSpots.get(i)
        holder.mIdView.text = item.getLocationName()
//        holder.mContentView.text = item.getLocationAddr()
        holder.mContentView.text = "" //item.getLocationAddr()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = parkingSpots.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
