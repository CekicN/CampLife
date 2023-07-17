package com.example.camplife.Fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.camplife.Login
import com.example.camplife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


class UserFragment : Fragment() {

    private lateinit var reference: DatabaseReference;

    private lateinit var userName:TextView;
    private lateinit var userEmail:TextView;
    private lateinit var userImage:CircleImageView;
    private lateinit var userLogout:ImageView;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_user, container, false)

        userName = view.findViewById(R.id.username);
        userEmail = view.findViewById(R.id.useremail);
        userImage = view.findViewById(R.id.userimage);
        userLogout = view.findViewById(R.id.logout);

        getData();

        userLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut();

            var intent = Intent(requireContext(), Login::class.java);

            startActivity(intent);
        }

        return view;
    }

    private fun getData() {
        reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnCompleteListener {
            if(it.isSuccessful)
            {
                if(it.result.exists())
                {
                    var dataSnap:DataSnapshot = it.result;
                    var username = dataSnap.child("username").getValue().toString();
                    var email = dataSnap.child("email").getValue().toString();
                    var imagePath:String = dataSnap.child("profileImage").getValue().toString();

                    userName.setText(username);
                    userEmail.setText(email);
                    Picasso.get()
                        .load(imagePath)
                        .into(userImage);
                }
            }
            else
            {

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            UserFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}