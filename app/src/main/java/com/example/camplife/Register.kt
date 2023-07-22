package com.example.camplife

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.camplife.Models.User
import com.example.camplife.databinding.ActivityRegisterBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID
import java.util.concurrent.TimeUnit

class Register : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding;
    private lateinit var firebaseAuth: FirebaseAuth;
    private lateinit var databaseReference: DatabaseReference;
    private lateinit var firebaseDatabase: FirebaseDatabase;

    private lateinit var imagePath:Uri;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater);
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        binding.signupButton.setOnClickListener{
            val username = binding.signupUsername.text.toString();
            val phone = binding.signupPhone.text.toString();
            val password = binding.signupPassword.text.toString();
            val confirmPassword = binding.signupConfirmPassword.text.toString();

            if(username.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty())
            {
                if(password == confirmPassword)
                {
                    val email = username + "@gmail.com"
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if(it.isSuccessful)
                        {
                            var user = User(username, "", "Address", phone,0);
                            FirebaseDatabase.getInstance().getReference("User")
                                .child(FirebaseAuth.getInstance().uid.toString())
                                .setValue(user).addOnCompleteListener{
                                    if(it.isSuccessful)
                                    {
                                        uploadImage();
                                        Toast.makeText(this, "Registration complete", Toast.LENGTH_SHORT).show();
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                    }
                                }
                        }
                        else
                        {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                    Toast.makeText(this, "password doesn't matched", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
        }
        binding.loginRediectText.setOnClickListener{
            val loginIntent = Intent(this, Login::class.java)
            startActivity(loginIntent);
        }

        binding.floatingActionButton.setOnClickListener{
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
            imagePath = data?.data!!
            binding.profileImage.setImageURI(imagePath);
        }
        else if(resultCode == ImagePicker.RESULT_ERROR)
            Toast.makeText(this,ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Task Cancelled", Toast.LENGTH_SHORT).show();

    }


    fun uploadImage()
    {
        if(imagePath != null) {
            var progres = ProgressDialog(this);
            progres.setTitle("Registration..")
            progres.show();

            FirebaseStorage.getInstance()
                .getReference("profileImages/" + FirebaseAuth.getInstance().currentUser?.uid.toString() + "/" + "image.jpg")
                .putFile(imagePath)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.getResult().storage.downloadUrl.addOnCompleteListener {
                            if (it.isSuccessful) {
                                updateProfileImage(it.getResult().toString());
                            }
                        };
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                    progres.dismiss();
                }
        }
    }

    private fun updateProfileImage(url: String) {
        FirebaseDatabase.getInstance().getReference("User")
            .child(FirebaseAuth.getInstance().uid.toString())
            .child("profileImage")
            .setValue(url);
    }
}