package kr.hyosang.autoshutdown

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class AutoShutdownApp() : Application() {
    override fun onCreate() {
        super.onCreate()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(
                    NotificationChannel(
                        Const.NotificationChannelId,
                        "AutoShutdown",
                        NotificationManager.IMPORTANCE_DEFAULT).also {
                        it.description = "Auto shutdown"
                        it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    }
                )
        }
    }

}