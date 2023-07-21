package com.example.camplife.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.camplife.R
import com.google.android.material.tabs.TabLayout


class MapContainerFragment : Fragment() {

    private lateinit var tabLayout:TabLayout;
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_map_container, container, false);

        tabLayout= view.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Camp List"));

        setFragment(MapFragment.newInstance())
        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    if(tab.position == 0)
                    {
                        setFragment(MapFragment.newInstance())
                    }
                    else
                    {
                        setFragment(MarkerListFragment.newInstance())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        return view;
    }
    public fun setFragment(fragment: Fragment)
    {
        val fragmentTransition = childFragmentManager.beginTransaction();
        fragmentTransition.replace(R.id.frameLayout, fragment).addToBackStack(Fragment::class.java.simpleName).commit();

    }

    fun setTab(tag:Int)
    {
        tabLayout.getTabAt(tag)?.select()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MapContainerFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}