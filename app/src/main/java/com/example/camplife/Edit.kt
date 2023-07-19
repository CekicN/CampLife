package com.example.camplife

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.camplife.databinding.ActivityEditBinding
import com.example.camplife.databinding.ActivityRegisterBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID
import kotlin.properties.Delegates

class Edit : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding;

    private lateinit var reference: DatabaseReference;

    private var username:String = "";
    private var imagePath:String = "";
    private var address:String = "";
    private var phoneNumber:String = "";

    private lateinit var uri: Uri;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater);
        setContentView(binding.root)

        reference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser?.uid.toString());

        showData();

        binding.save.setOnClickListener{
            isUsernameChanged { result ->
                if(result or isAddressChanged() or isPhoneChanged())
                {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                }
                var intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
            }
        }

        binding.editPhoto.setOnClickListener{
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start(20);
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK)
        {
            uri = data?.data!!
            binding.editImage.setImageURI(uri);
            editImage();
        }
        else if(resultCode == ImagePicker.RESULT_ERROR)
            Toast.makeText(this,ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Task Cancelled", Toast.LENGTH_SHORT).show();

    }
    fun editImage()
    {
        var progres = ProgressDialog(this);
        progres.setTitle("Uploading...")
        progres.show();

        FirebaseStorage.getInstance().getReference("profileImages/"+ FirebaseAuth.getInstance().currentUser?.uid.toString()).child("image.jpg").putFile(uri)
            .addOnCompleteListener {
                if(it.isSuccessful)
                {
                    it.getResult().storage.downloadUrl.addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            updateProfileImage(it.getResult().toString());
                        }
                    };
                }
                else
                {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                }
                progres.dismiss();
            }
    }

    private fun updateProfileImage(url: String) {
        FirebaseDatabase.getInstance().getReference("User")
            .child(FirebaseAuth.getInstance().uid.toString())
            .child("profileImage")
            .setValue(url);
    }

    private fun isUsernameChanged(callback:(Boolean) -> Unit)
    {
        if(!username.equals(binding.editUsername.text.toString())){
            val email = binding.editUsername.text.toString() + "@gmail.com";
            FirebaseAuth.getInstance().currentUser?.updateEmail(email)?.addOnCompleteListener {
                if(it.isSuccessful)
                {
                    reference.child("username").setValue(binding.editUsername.text.toString());
                    username = binding.editUsername.text.toString();
                    callback(true);
                }
                else
                {
                    callback(false)
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                }
            };
        }
        callback(false);
    }
    private fun isAddressChanged():Boolean
    {
        if(!address.equals(binding.editAddress.text.toString())){
            reference.child("address").setValue(binding.editAddress.text.toString());
            address = binding.editAddress.text.toString();
            return true;
        }
        return false;
    }

    private fun isPhoneChanged():Boolean
    {
        if(!phoneNumber.equals(binding.editPhone.text.toString())){
            reference.child("phoneNumber").setValue(binding.editPhone.text.toString());
            phoneNumber = binding.editPhone.text.toString();
            return true;
        }
        return false;
    }

    private fun showData()
    {
        imagePath = intent.getStringExtra("imagePath").toString();
        username = intent.getStringExtra("username").toString();
        address = intent.getStringExtra("address").toString();
        phoneNumber = intent.getStringExtra("phoneNumber").toString();


        Picasso.get()
            .load(imagePath)
            .into(binding.editImage);
        binding.editUsername.setText(username);
        binding.editAddress.setText(address);
        binding.editPhone.setText(phoneNumber);

    }

}