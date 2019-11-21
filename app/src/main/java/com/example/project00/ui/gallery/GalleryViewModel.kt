package com.example.project00.ui.gallery

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GalleryViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Service enable"
    }
    val text: LiveData<String> = _text
    var screenFlag : Boolean? = true


}