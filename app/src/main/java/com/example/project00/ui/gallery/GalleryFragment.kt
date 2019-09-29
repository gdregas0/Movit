package com.example.project00.ui.gallery

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.project00.R
import com.example.project00.api.SensorService

class GalleryFragment : Fragment(), SensorEventListener {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* View model object created
        * */
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        Log.e("Debug", "eyedi onResume start service")
        val AccelService = context?.startService(Intent(context, SensorService::class.java))
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("Debug", "Accuracy changed ?")
    }

    override fun onSensorChanged(p0: SensorEvent?) {
    }
}