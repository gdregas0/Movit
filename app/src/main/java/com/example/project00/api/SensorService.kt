package com.example.project00.api

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.admin.DevicePolicyManager
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


class SensorService : Service(), SensorEventListener {
    private lateinit var sensorViewModel: GalleryViewModel
    private lateinit var wlock : PowerManager.WakeLock

    private val sensorMNG: SensorManager by lazy {
        Log.i("Info", "initialize sensor manager")
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val devicepolicyMNG: DevicePolicyManager by lazy {
        getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    private val powerMNG : PowerManager by lazy {
        getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    /* companion object same as static */
    companion object {
        const val EXTRA_NOTIFICATION_REQUEST_CODE = "EXTRA_NOTIFICATION_REQUEST_CODE"
        const val EXTRA_NOTIFICATION = "EXTRA_NOTIFICATION"

        fun stop(context: Context) {
            val intent = Intent(context, SensorService::class.java)
            context.stopService(intent)
        }

        fun showNotification(
            context: Context,
            requestCode: Int,
            notification: Notification
        ): Boolean {
            val intent = Intent(context, SensorService::class.java)
            intent.putExtra(EXTRA_NOTIFICATION_REQUEST_CODE, requestCode)
            intent.putExtra(EXTRA_NOTIFICATION, notification)
            return startService(context, intent)
        }

        private fun startService(context: Context, intent: Intent): Boolean {
            // Similar to ContextCompat.startForegroundService(context, intent)
            val componentName: ComponentName? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            return componentName != null
        }

        /* Overloading createNotificationChannel
        * if you couldn't get NotificationChannel at parameter
        * function which is createNotificationChannel make NotificationChannel obj
        * */
        @RequiresApi(api = 26)
        fun createNotificationChannel(
            context: Context,
            id: String, name: String, importance: Int,
            description: String
        ) {
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            createNotificationChannel(context, channel)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        @RequiresApi(api = 26)
        fun createNotificationChannel(
            context: Context,
            channel: NotificationChannel
        ) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    } /* end of companion obj */


    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("[eyedi] Not yet...")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("Debug", "sensor_service unbind sensor_manager")
        sensorMNG.unregisterListener(this)
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                .create(GalleryViewModel::class.java)

        sensorMNG.registerListener(
            this,
            sensorMNG.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        wlock = powerMNG.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                or PowerManager.ON_AFTER_RELEASE
                or PowerManager.SCREEN_BRIGHT_WAKE_LOCK
            , "project00:wakelockTag")

        try {
            if (intent == null) {
                Log.d("Debug", "intent is null")
                return START_NOT_STICKY
            }

            val extras = intent.extras
            if (extras == null) {
                Log.d("Debug", "intent extras is null")
                return START_NOT_STICKY
            }
            if (extras.containsKey(EXTRA_NOTIFICATION)) {
                val notification = extras.getParcelable<Parcelable>(EXTRA_NOTIFICATION)
                if (notification is Notification) {
                    if (extras.containsKey(EXTRA_NOTIFICATION_REQUEST_CODE)) {
                        val requestCode = extras.getInt(EXTRA_NOTIFICATION_REQUEST_CODE)
                        startForeground(requestCode, notification)
                    }
                }
            }
        } catch (e : Exception){
            Log.e("Error", "Fail to foreground service created ")
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
        /* Z-value determine on/off
        * value > 5  : Screen On
        * value < -5 : Screen Off
        */
        if ((event!!.values[2] > 5) && (sensorViewModel.screenFlag == false)
        ) {
            sensorViewModel.screenFlag = true
            wlock.acquire(50)
            wlock.release()
            Log.d("Debug", "screen on z: " + event.values[2])

        } else if ((event.values[2] < -5) && (sensorViewModel.screenFlag == true)
        ) {
            sensorViewModel.screenFlag = false
            devicepolicyMNG.lockNow()
            Log.d("Debug", "screen off z: " + event.values[2])
        }
    }
}
