package com.example.camplife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.etebarian.meowbottomnavigation.MeowBottomNavigation


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation);
        bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.baseline_star_24));
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.baseline_map_24));
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.baseline_perm_identity_24));

        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                0->{
                 Toast.makeText(this, "StarFragment", Toast.LENGTH_SHORT).show();
                }
                1-> {
                    Toast.makeText(this, "MapFragment", Toast.LENGTH_SHORT).show();
                }
                2-> {
                    Toast.makeText(this, "ProfileFragment", Toast.LENGTH_SHORT).show();
                }
                else->{
                    Toast.makeText(this, "MapFragment", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}