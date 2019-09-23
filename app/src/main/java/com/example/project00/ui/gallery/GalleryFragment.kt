package com.example.project00.ui.gallery

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import kotlin.concurrent.thread

class GalleryFragment : Fragment(), SensorEventListener {

    private lateinit var galleryViewModel: GalleryViewModel

    private val sensorMNG : SensorManager by lazy {
        activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        Log.e("Debug", "eyedi onResume")

        sensorMNG.registerListener(this,
            sensorMNG.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorMNG.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        Log.e("Debug", "x : " + p0!!.values[0]+
                " y: " + p0!!.values[1]+
                " z: " + p0!!.values[2])
    }
}