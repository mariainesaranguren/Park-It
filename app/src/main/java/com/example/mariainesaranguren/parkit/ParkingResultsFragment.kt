package com.example.mariainesaranguren.parkit

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ParkingResultsFragment.OnListFragmentInteractionListener] interface.
 */
class ParkingResultsFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var parkingSpacesList: ArrayList<ParkingLocation> = ArrayList()
    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            parkingSpacesList = it.getParcelableArrayList(ParkingResultsFragment.ARG_PARKING_SPACES_ARRAY_LIST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_parkingresults_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyParkingResultsRecyclerViewAdapter(parkingSpacesList, listener)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
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
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentIteraction(item: ParkingLocation)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_PARKING_SPACES_ARRAY_LIST = "parking-spaces-array-list"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int, parkingSpacesList : ArrayList<ParkingLocation>) =
                ParkingResultsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                        putParcelableArrayList(ParkingResultsFragment.ARG_PARKING_SPACES_ARRAY_LIST, parkingSpacesList)
                    }
                }
    }
}
