package kr.hyosang.autoshutdown

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class PowerMonService: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(receiver, IntentFilter(Intent.ACTION_POWER_DISCONNECTED))
        registerReceiver(receiver, IntentFilter(Const.AbortShutdownBroadcast))

        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(Intent.ACTION_POWER_DISCONNECTED == p1?.action) {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .notify(0, Notification.Builder(p0, Const.NotificationChannelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Device will shutdown")
                        .setContentText("Device will shutdown in xx sec")
                        .setTicker("Device will shutdown")
                        .setAutoCancel(true)
                        .setContentIntent(
                            PendingIntent.getBroadcast(p0, 0, Intent(Const.AbortShutdownBroadcast), 0)
                        )
                        .build()
                    )






                //DelayedShutdown(p0).start()
            }else if(Const.AbortShutdownBroadcast == p1?.action) {
                Log.i("TEST", "ABORT!")
            }
        }
    }

    private class DelayedShutdown(
        context: Context?,
        private val applicationContext: Context? = context?.applicationContext
    ): Thread() {
        override fun run() {
            var i = Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN")
            i.putExtra("android.intent.extra.KEY_CONFIRM", false)
            i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            applicationContext?.startActivity(i)
        }
    }
}