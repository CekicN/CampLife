package com.example.camplife.Fragments

import ManagePermissions
import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.camplife.MainActivity
import com.example.camplife.Models.MarkerModel
import com.example.camplife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID
import kotlin.properties.Delegates


class AddMarkerFragment : Fragment() {

    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private lateinit var  imageReference:StorageReference;
    private lateinit var  reference:DatabaseReference;

    private lateinit var imageList:ArrayList<SlideModel>;
    private lateinit var pathList:ArrayList<String>;
    private lateinit var randKey:String;

    private lateinit var campName: EditText;
    private lateinit var address:EditText;
    private lateinit var phone:EditText;
    private lateinit var description:EditText;
    private lateinit var uploadImages: Button;
    private lateinit var slider: ImageSlider;
    private lateinit var cancel:Button;
    private lateinit var add:Button;

    private var lat: Double? = null;
    private var lng: Double? = null;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_add_marker, container, false)
        val list = listOf<String>(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        managePermissions = ManagePermissions(requireActivity(),list,PermissionsRequestCode)
        imageReference = FirebaseStorage.getInstance().getReference("campMarkerImages/" + FirebaseAuth.getInstance().currentUser?.uid.toString())
        reference = FirebaseDatabase.getInstance().getReference("campMarkers");

        imageList = ArrayList();
        pathList = ArrayList();
        randKey = UUID.randomUUID().toString();

        campName = view.findViewById(R.id.addmarker_campname);
        address = view.findViewById(R.id.addmarker_address);
        phone = view.findViewById(R.id.addmarker_phone);
        description = view.findViewById(R.id.addmarker_description);
        slider = view.findViewById<ImageSlider>(R.id.slider);
        uploadImages = view.findViewById(R.id.addmarker_uploadImages);
        cancel = view.findViewById(R.id.addmarker_cancel);
        add = view.findViewById(R.id.addmarker_add);

        lat = arguments?.getDouble("lat");
        lng = arguments?.getDouble("lng");

        uploadImages.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                managePermissions.checkPermissions()
            openGalleryForImages()
        }
        add.setOnClickListener{
            var campNameText = campName.text.toString();
            var addressText = address.text.toString();
            var phoneText = phone.text.toString();
            var descriptionText = description.text.toString();

            if(campNameText.isNotEmpty() && phoneText.isNotEmpty() && addressText.isNotEmpty() && descriptionText.isNotEmpty())
            {
                var marker = MarkerModel(randKey,campNameText, phoneText, addressText, descriptionText, pathList, lat, lng);
                reference.child(randKey).setValue(marker).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val nextFrag = MapFragment.newInstance();
                        (activity as MainActivity).setFragment(nextFrag);
                        Toast.makeText(requireContext(), "Camp added", Toast.LENGTH_SHORT).show();
                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show();
                    }
            }
            else
            {
                Toast.makeText(requireContext(), "Fields can not be empty", Toast.LENGTH_SHORT).show();
            }
        }
        cancel.setOnClickListener{
            if(pathList.size > 0)
            {
                for (i in 0..pathList.size!! - 1) {
                    imageReference.child(randKey).child("${i}.jpg").delete().addOnCompleteListener {

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            val nextFrag = MapFragment.newInstance();
            (activity as MainActivity).setFragment(nextFrag);
        }
        return view;
    }
    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures")
                , 200
            )
        }
        else {
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 200);
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 200){

            if (data?.getClipData() != null) {
                var count = data.clipData?.itemCount
                var progres = ProgressDialog(requireContext());
                progres.setTitle("Uploading...")
                progres.show();
                var uploadCounter = 0;
                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    uploadToStorage(imageUri, i, randKey){
                            uploadCounter++
                            if (uploadCounter == count) {
                                progres.dismiss()
                                slider.setImageList(imageList)
                            }
                    }
                }
            }
        }
    }



    private fun uploadToStorage(uri:Uri, counter:Int, rand:String, onComplete: () -> Unit) {
        if (uri != null) {
                try {
                    imageReference.child("${rand}/" + "${counter}.jpg")
                        .putFile(uri)
                        .addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                it.getResult().storage.downloadUrl.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        pathList.add(it.getResult().toString());
                                        imageList.add(SlideModel(it.getResult().toString()));
                                    }
                                    onComplete()
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show();
                            onComplete()
                        }

                }
                catch (e:Exception)
                {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddMarkerFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}