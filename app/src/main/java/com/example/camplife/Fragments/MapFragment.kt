package com.example.camplife.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.camplife.MainActivity
import com.example.camplife.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MapFragment : Fragment(),OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location;
    private lateinit var fusedLocation: FusedLocationProviderClient

    private lateinit var addMarker:FloatingActionButton;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_map, container, false)
        addMarker = view.findViewById(R.id.addMarker);
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity());

        addMarker.setOnClickListener{
            val nextFrag = AddMarkerFragment.newInstance();
            (activity as MainActivity).setFragment(nextFrag);
        }
        return view;
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        @JvmStatic
        fun newInstance() =
            MapFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.setOnMarkerClickListener(this)

        setUpMap();
    }

    private fun setUpMap()
    {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocation.lastLocation.addOnSuccessListener {
            if(it != null)
            {
                lastLocation = it
                val currentLatLng = LatLng(it.latitude, it.longitude);
//                placeMakerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun placeMakerOnMap(currentLatLng: LatLng) {
    val markerOption = MarkerOptions().position(currentLatLng)
        markerOption.title("HI")
        mMap.addMarker(markerOption);
    }

    override fun onMarkerClick(marker: Marker) = false
}