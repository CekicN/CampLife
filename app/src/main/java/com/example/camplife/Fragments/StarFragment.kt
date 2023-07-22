package com.example.camplife.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.camplife.Adapters.MarkerListAdapter
import com.example.camplife.Adapters.UserListAdapter
import com.example.camplife.Models.MarkerModel
import com.example.camplife.Models.User
import com.example.camplife.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StarFragment : Fragment() {

    private lateinit var userList:ArrayList<User>;
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listView: ListView;
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_star, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        listView = view.findViewById(R.id.listView);
        userList = ArrayList();

        getData()
        return view
    }

    private fun getData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for (userSnapshot in snapshot.children)
                    {
                        //preuzimanje podataka i punjnje liste
                        val username = userSnapshot.child("username").getValue().toString();
                        val address = userSnapshot.child("address").getValue().toString();
                        val phone = userSnapshot.child("phoneNumber").getValue().toString();
                        val profileImage = userSnapshot.child("profileImage").getValue().toString();
                        val points = userSnapshot.child("points").getValue().toString().toInt();

                        userList.add(User(username, profileImage, address,phone, points));
                    }
                    userList.sortByDescending {
                        it.points
                    }

                    listView.isClickable = true;
                    listView.adapter = UserListAdapter(requireActivity(), userList);


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StarFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}