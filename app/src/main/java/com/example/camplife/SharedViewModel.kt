package com.example.camplife

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient

class SharedViewModel: ViewModel() {

    val sharedData: MutableLiveData<Location> = MutableLiveData();

    fun setLocation(loc:Location)
    {
        this.sharedData.value = loc;
    }
}