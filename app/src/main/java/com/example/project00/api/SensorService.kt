package com.example.project00.api

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.project00.ui.gallery.GalleryViewModel
import java.lang.UnsupportedOperationException

class SensorService : Service(), SensorEventListener {
    private lateinit var sensorViewModel: GalleryViewModel

    private val pwrMNG : PowerManager by lazy {
        getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private val sensorMNG : SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException ("Not yet")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        sensorMNG.unregisterListener(this)

        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorViewModel =
            ViewModelProvider.
                AndroidViewModelFactory.getInstance(application).
                create(GalleryViewModel::class.java)

        sensorMNG.registerListener(this,
            sensorMNG.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.i("Debug", "destroyed")
        super.onDestroy()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("Debug", "Accuracy changed ?")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        /* Z-value determine on/off */
        if ((event!!.values[2] > 5) &&
            (sensorViewModel.screenFlag == false)
        ) {

            sensorViewModel.screenFlag = true
            Log.d("Debug", " z: " + event!!.values[2])


        } else if ((event!!.values[2] < -5) &&
            (sensorViewModel.screenFlag == true)
        ) {

            sensorViewModel.screenFlag = false
            Log.d("Debug", " z: " + event!!.values[2])

        }
    }
}
