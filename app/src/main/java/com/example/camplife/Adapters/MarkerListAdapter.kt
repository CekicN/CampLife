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
import com.example.camplife.R
import com.squareup.picasso.Picasso

class MarkerListAdapter(private val context:Activity, private val arrayList:ArrayList<MarkerModel>):ArrayAdapter<MarkerModel>(context,
R.layout.list_item, arrayList) {
    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater:LayoutInflater = LayoutInflater.from(context)
        val view:View = inflater.inflate(R.layout.list_item, null);

        val imageView: ImageView = view.findViewById<ImageView>(R.id.list_image);
        val campName: TextView = view.findViewById<TextView>(R.id.list_campname);
        val campAddress: TextView = view.findViewById<TextView>(R.id.list_address);
        val campPhone: TextView = view.findViewById<TextView>(R.id.list_phone);

        Picasso.get()
            .load(arrayList[position].imagePaths[0])
            .into(imageView);

        campName.setText(arrayList[position].campName);
        campAddress.setText(arrayList[position].address);
        campPhone.setText(arrayList[position].phone);

        return view;
    }
}