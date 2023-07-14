package com.example.camplife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.camplife.Models.User
import com.example.camplife.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding;
    private lateinit var firebaseAuth: FirebaseAuth;
    private lateinit var databaseReference: DatabaseReference;
    private lateinit var firebaseDatabase: FirebaseDatabase;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater);
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        binding.signupButton.setOnClickListener{
            val username = binding.signupUsername.text.toString();
            val email = binding.signupEmail.text.toString();
            val password = binding.signupPassword.text.toString();
            val confirmPassword = binding.signupConfirmPassword.text.toString();

            if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty())
            {
                if(password == confirmPassword)
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if(it.isSuccessful)
                        {
                            var user = User(username, email);
                            FirebaseDatabase.getInstance().getReference("User")
                                .child(FirebaseAuth.getInstance().uid.toString())
                                .setValue(user).addOnCompleteListener{
                                    if(it.isSuccessful)
                                    {
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
    }
}