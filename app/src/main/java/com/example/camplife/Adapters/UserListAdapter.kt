package com.example.camplife.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.camplife.Models.MarkerModel
import com.example.camplife.Models.User
import com.example.camplife.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(private val context:Activity, private val arrayList:ArrayList<User>):ArrayAdapter<User>(context,
    R.layout.list_item, arrayList) {
    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater:LayoutInflater = LayoutInflater.from(context)
        val view:View = inflater.inflate(R.layout.user_list_item, null);

        val imageView: CircleImageView = view.findViewById<CircleImageView>(R.id.user_listimage);
        val username: TextView = view.findViewById<TextView>(R.id.list_username);
        val address: TextView = view.findViewById<TextView>(R.id.list_address);
        val phone: TextView = view.findViewById<TextView>(R.id.list_phone);
        val points:TextView = view.findViewById<TextView>(R.id.list_points);
        Picasso.get()
            .load(arrayList[position].profileImage)
            .into(imageView);

        username.setText(arrayList[position].username);
        address.setText(arrayList[position].address);
        phone.setText(arrayList[position].phoneNumber);
        points.setText(arrayList[position].points.toString());

        return view;
    }
}