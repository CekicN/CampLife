package com.example.camplife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.camplife.Fragments.MapFragment
import com.example.camplife.Fragments.StarFragment
import com.example.camplife.Fragments.UserFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation);
        setFragment(MapFragment.newInstance());
        bottomNavigation.show(1);
        bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.baseline_star_24));
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.baseline_map_24));
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.baseline_perm_identity_24));

        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                0->{
                    setFragment(StarFragment.newInstance());
                }
                1-> {
                    setFragment(MapFragment.newInstance());
                }
                2-> {
                    setFragment(UserFragment.newInstance());
                }
                else->{
                    setFragment(MapFragment.newInstance());
                }
            }
        }
    }
    private fun setFragment(fragment: Fragment)
    {
        val fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.replace(R.id.frameLayout, fragment).addToBackStack(Fragment::class.java.simpleName).commit();

    }
}