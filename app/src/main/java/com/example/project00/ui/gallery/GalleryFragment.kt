package com.example.project00.ui.gallery

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.project00.R
import com.example.project00.api.SensorService

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    companion object {
        private const val TAG = "MainApplication"

        fun getMainApplication(context: Context): GalleryFragment {
            return context.applicationContext as GalleryFragment
        }

        private const val NOTIFICATION_REQUEST_CODE = 100
        private const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
    }

    private lateinit var notification: Notification

    var isNotificationShowing: Boolean = false
        private set

    @SuppressLint("InlinedApi")
    @RequiresApi(api = 26)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* View model object created */
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
        })

        if (Build.VERSION.SDK_INT >= 26) {
            val appName = getString(R.string.app_name)
            val channelName = "$appName channel name"
            val channelImportance = NotificationManager.IMPORTANCE_LOW
            val channelDescription = "$appName channel description"

            SensorService.createNotificationChannel(super.getContext() as Context,
                NOTIFICATION_CHANNEL_ID,
                channelName,
                channelImportance,
                channelDescription)
        }
        notification = createOngoingNotification(NOTIFICATION_REQUEST_CODE, R.drawable.ic_notification, "Content Text")

        return root
    }

    override fun onResume() {
        super.onResume()
        Log.e("Debug", "eyedi onResume start service")
        val AccelService = context?.startService(Intent(context, SensorService::class.java))
    }

    @RequiresApi(api = 26)
    private fun createOngoingNotification(requestCode: Int, icon: Int, text: String): Notification {

        val context: Context = super.getContext() as Context

        val contentIntent = Intent(context, GalleryFragment::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentPendingIntent = PendingIntent.getActivity(context, requestCode, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(contentPendingIntent)
            .build()
    }

    fun showNotification(show: Boolean) {
        if (show) {
            SensorService.showNotification(super.getContext() as Context, NOTIFICATION_REQUEST_CODE, notification)
            isNotificationShowing = true
        } else {
            isNotificationShowing = false
            SensorService.stop(super.getContext() as Context)
        }
    }
}