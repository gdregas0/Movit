package com.example.project00.api

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build

import android.os.IBinder
import android.os.Parcelable
import android.os.PowerManager

import android.util.Log
import androidx.annotation.RequiresApi
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

    companion object {
        private const val TAG = "TestService"

        const val EXTRA_NOTIFICATION_REQUEST_CODE = "EXTRA_NOTIFICATION_REQUEST_CODE"
        const val EXTRA_NOTIFICATION = "EXTRA_NOTIFICATION"

        fun showNotification(context: Context, requestCode: Int, notification: Notification): Boolean {
            val intent = Intent(context, SensorService::class.java)
            intent.putExtra(EXTRA_NOTIFICATION_REQUEST_CODE, requestCode)
            intent.putExtra(EXTRA_NOTIFICATION, notification)
            return startService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, SensorService::class.java)
            context.stopService(intent)
        }

        private fun startService(context: Context, intent: Intent): Boolean {
            // Similar to ContextCompat.startForegroundService(context, intent)
            val componentName: ComponentName? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            return componentName != null
        }

        @RequiresApi(api = 26)
        fun createNotificationChannel(context: Context,
                                      id: String, name: String, importance: Int,
                                      description: String) {
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            createNotificationChannel(context, channel)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        @RequiresApi(api = 26)
        fun createNotificationChannel(context: Context,
                                      channel: NotificationChannel) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    } /* end of companion obj */

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException ("Not yet")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("Debug", "sensor_service unbind sensor_manager")
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

        try {
            if (intent != null) {
                val extras = intent.extras
                if (extras != null) {
                    if (extras.containsKey(EXTRA_NOTIFICATION)) {
                        val notification = extras.getParcelable<Parcelable>(EXTRA_NOTIFICATION)
                        if (notification is Notification) {
                            if (extras.containsKey(EXTRA_NOTIFICATION_REQUEST_CODE)) {
                                val requestCode = extras.getInt(EXTRA_NOTIFICATION_REQUEST_CODE)
                                startForeground(requestCode, notification)
                            }
                        }
                    }
                }
            }

            return START_NOT_STICKY
        } finally {
            Log.e("Debug", "Fail to foreground service created")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.i("Debug", "destroyed")
        //PbLog.s(TAG, PbStringUtils.separateCamelCaseWords("onDestroy"));
        stopForeground(true)
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
            // maybe.. i could make window on/off function at here
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
