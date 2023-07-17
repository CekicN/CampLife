package com.example.camplife.Fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.viewbinding.ViewBindings
import com.example.camplife.Edit
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
    private lateinit var userAddress:TextView;
    private lateinit var userPhone:TextView;
    private lateinit var userImage:CircleImageView;
    private lateinit var userLogout:ImageView;

    private var username:String = "";
    private var email:String = "";
    private var imagePath:String = "";
    private var address:String = "";
    private var phoneNumber:String = "";
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_user, container, false)

        userName = view.findViewById(R.id.username);
        userEmail = view.findViewById(R.id.useremail);
        userAddress = view.findViewById(R.id.useraddress);
        userPhone = view.findViewById(R.id.userphone);
        userImage = view.findViewById(R.id.userimage);
        userLogout = view.findViewById(R.id.logout);
        var btn = view.findViewById<Button>(R.id.edit);
        getData();

        btn.setOnClickListener{
            edit();
        }
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
                    username = dataSnap.child("username").getValue().toString();
                    email = dataSnap.child("email").getValue().toString();
                    imagePath = dataSnap.child("profileImage").getValue().toString();
                    address = dataSnap.child("address").getValue().toString();
                    phoneNumber = dataSnap.child("phoneNumber").getValue().toString();

                    userName.setText(username);
                    userEmail.setText(email);
                    userAddress.setText(address);
                    userPhone.setText(phoneNumber);
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
    private fun edit()
    {
        var intent = Intent(requireContext(), Edit::class.java);

        intent.putExtra("imagePath", imagePath);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("address", address);
        intent.putExtra("phoneNumber", phoneNumber);

        startActivity(intent);
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