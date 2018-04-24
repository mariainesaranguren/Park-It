package com.example.mariainesaranguren.parkit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_start_navigation.*
import kotlinx.android.synthetic.main.fragment_start_navigation.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_LOCATION = "parking_location"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StartNavigationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StartNavigationFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class StartNavigationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var parking_location: ParkingLocation
    private var listener: OnFragmentInteractionListener? = null

    // Fragment components
    private lateinit var start_button: Button
    private lateinit var cancel_trip_button: Button
    private lateinit var lot_name_textview: TextView
    private lateinit var lot_addr_textview: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parking_location = it.getParcelable(ARG_LOCATION) as ParkingLocation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view: View = inflater.inflate(R.layout.fragment_start_navigation, container, false)
        lot_name_textview = view.nav_lot_name
        lot_addr_textview = view.nav_lot_addr
        start_button = view.nav_button

        lot_name_textview.text = parking_location.getLocationName()
        lot_addr_textview.text = parking_location.getLocationAddr()

        // Add onClick listener for button
        start_button.setOnClickListener {
            Log.d("StartNavigationFragment", "Start nav button clicked")
            listener?.beginNavigation(parking_location)
        }

        cancel_trip_button = view.cancel_button
        // Add onClick listener for button
        cancel_trip_button.setOnClickListener {
            Log.d("StartNavigationFragment", "Start nav button clicked")
            listener?.cancelNavigation()
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun prepareForNavigation(location: ParkingLocation)
        fun beginNavigation(location: ParkingLocation)
        fun cancelNavigation()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StartNavigationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: ParkingLocation) =
                StartNavigationFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_LOCATION, param1)
                    }
                }
    }
}
