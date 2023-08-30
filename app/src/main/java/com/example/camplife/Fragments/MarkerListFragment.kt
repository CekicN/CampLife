package com.example.camplife.Fragments

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.camplife.Adapters.MarkerListAdapter
import com.example.camplife.MainActivity
import com.example.camplife.Models.MarkerModel
import com.example.camplife.R
import com.example.camplife.SharedViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.slider.Slider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference


class MarkerListFragment : Fragment() {

    private lateinit var markerList:ArrayList<MarkerModel>;
    private lateinit var databaseReference:DatabaseReference
    private lateinit var listView:ListView;
    private lateinit var slider: Slider;
    private lateinit var sharedViewModel: SharedViewModel;
    private lateinit var currLocation: Location;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_marker_list, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.sharedData.observe(viewLifecycleOwner, Observer {
            currLocation = it
        })
        databaseReference = FirebaseDatabase.getInstance().getReference("campMarkers")
        listView = view.findViewById(R.id.listView);
        slider = view.findViewById(R.id.rangeSlider);
        markerList = ArrayList();

        getData(0.0);
        slider.addOnChangeListener { slider, value, fromUser ->
            markerList = ArrayList();
            getData(value.toDouble());
        }
        return view
    }

    private fun getData(value:Double) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for (campSnapshot in snapshot.children)
                    {
                        val lat: String = campSnapshot.child("latitude").value.toString()
                        val lng: String = campSnapshot.child("longitude").value.toString()
                        val latitude = lat.toDouble()
                        val longitude = lng.toDouble()

                        var inRadius = haversineDistance(latitude, longitude, currLocation.latitude, currLocation.longitude);
                        Log.d("RADIUS", inRadius.toString());
                        val loc = LatLng(latitude, longitude)
                        val campId = campSnapshot.child("postId").value.toString()

                        val campName = campSnapshot.child("campName").value.toString()
                        val campAddress = campSnapshot.child("address").getValue().toString()
                        val campPhone = campSnapshot.child("phone").getValue().toString()
                        val listaSlika:ArrayList<String> = ArrayList();
                        campSnapshot.child("imagePaths").children.forEach {
                            listaSlika.add(it.value.toString());
                        }
                        if(inRadius < value || value == 0.0)
                        {
                            markerList.add(MarkerModel("",  campName,campPhone, campAddress,"",listaSlika, latitude, longitude,""));
                        }
                    }

                    listView.isClickable = true;
                    listView.adapter = MarkerListAdapter(requireActivity(), markerList);

                    listView.setOnItemClickListener { parent, view, position, id ->
                        val la: Double? = markerList[position].latitude;
                        val ln: Double? = markerList[position].longitude;


                        val nextFrag = MapFragment.newInstance();
                            val args = Bundle()

                            if (la != null) {
                                args.putDouble("lat", la)
                            }
                            if (ln != null) {
                                args.putDouble("lng", ln)
                            }
                        nextFrag.arguments = args


                        val mapContainerFragment =
                            requireActivity().supportFragmentManager.findFragmentById(R.id.frameLayout) as MapContainerFragment?
                        if (mapContainerFragment != null) {
                            mapContainerFragment.setTab(0)
                        };
                        if (mapContainerFragment != null) {
                            mapContainerFragment.setFragment(nextFrag)
                        };
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MarkerListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}

fun haversineDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val R = 6371.0

    val lat = Math.toRadians(lat2 - lat1)
    val lng = Math.toRadians(lng2 - lng1)

    val a = Math.pow(Math.sin(lat / 2), 2.0) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(lng / 2), 2.0)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    val distance = R * c
    return distance
}