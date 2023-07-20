package com.example.camplife.Fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.camplife.Fragments.AddMarkerFragment.Companion.newInstance
import com.example.camplife.MainActivity
import com.example.camplife.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class MapFragment : Fragment(),OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location;
    private lateinit var fusedLocation: FusedLocationProviderClient

    private lateinit var addMarker:FloatingActionButton;

    private lateinit var CampName:TextView;
    private lateinit var CampAddress:TextView;
    private lateinit var CampPhone:TextView;
    private lateinit var CampDescription:TextView;
    private lateinit var CampSlider:ImageSlider;

    private lateinit var databaseReference: DatabaseReference;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_map, container, false)


        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        addMarker = view.findViewById(R.id.addMarker);
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference("campMarkers")
        addMarker.setOnClickListener{
            val lat = lastLocation.latitude;
            val lng = lastLocation.longitude;

            val nextFrag = AddMarkerFragment.newInstance();
            val args = Bundle()
            args.putDouble("lat", lat);
            args.putDouble("lng", lng);
            nextFrag.arguments = args;

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

        setUpMap();
        getMarkers();
        mMap.setOnMarkerClickListener(OnMarkerClickListener {
            val id = it.tag as String?
            if (!id.isNullOrEmpty()) {
                Toast.makeText(requireContext(), id, Toast.LENGTH_SHORT).show();
                showDialog(id);
            }
            false
        })
    }

    private fun showDialog(id:String) {
        val dialog = Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        CampName = dialog.findViewById(R.id.dialog_campname);
        CampAddress = dialog.findViewById(R.id.dialog_address);
        CampPhone = dialog.findViewById(R.id.dialog_phone);
        CampSlider = dialog.findViewById(R.id.dialog_slider);
        CampDescription = dialog.findViewById(R.id.dialog_description);

        databaseReference.child(id).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.exists()) {
                    var dataSnap: DataSnapshot = it.result;
                    CampName.setText(dataSnap.child("campName").getValue().toString())
                    CampAddress.setText(dataSnap.child("address").getValue().toString())
                    CampPhone.setText(dataSnap.child("phone").getValue().toString())
                    CampDescription.setText(dataSnap.child("description").getValue().toString())
                    var lista = ArrayList<SlideModel>();
                    for(image in dataSnap.child("imagePaths").children)
                    {
                        lista.add(SlideModel(image.value.toString()))
                    }
                    CampSlider.setImageList(lista);
                }
            }
        }

        dialog.show();
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM);
    }

    private fun getMarkers() {
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
                        val loc = LatLng(latitude, longitude)
                        val campId = campSnapshot.child("postId").value.toString()

                        placeMakerOnMap(loc, campId);
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun placeMakerOnMap(loc: LatLng, markerId:String) {
        val mMarker = mMap.addMarker(
            MarkerOptions().position(loc)
                .icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.tent)
                )
        )

        mMarker?.tag = markerId;
    }
}
