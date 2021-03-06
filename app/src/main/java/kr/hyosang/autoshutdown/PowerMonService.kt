package kr.hyosang.autoshutdown

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log

class PowerMonService: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(receiver, IntentFilter(Intent.ACTION_POWER_DISCONNECTED))
        registerReceiver(receiver, IntentFilter(Intent.ACTION_POWER_CONNECTED))
        registerReceiver(receiver, IntentFilter(Const.AbortShutdownBroadcast))

        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(Intent.ACTION_POWER_DISCONNECTED == p1?.action) {
                val delay = AppPref.instance.delaySec
                val builder: Notification.Builder = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Notification.Builder(p0, Const.NotificationChannelId)
                }else {
                    Notification.Builder(p0)
                }
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .notify(
                        0, builder.setSmallIcon(android.R.drawable.ic_lock_power_off)
                            .setContentTitle("Device will shutdown")
                            .setContentText("Device will shutdown in $delay sec. Tap notification to stop.")
                            .setTicker("Device will shutdown")
                            .setAutoCancel(true)
                            .setContentIntent(
                                PendingIntent.getBroadcast(
                                    p0,
                                    0,
                                    Intent(Const.AbortShutdownBroadcast),
                                    0
                                )
                            )
                            .build()
                    )

                DelayedShutdown(p0, delay).start()
            }else if(Intent.ACTION_POWER_CONNECTED == p1?.action) {
                DelayedShutdown.stopped = true
            }else if(Const.AbortShutdownBroadcast == p1?.action) {
                Log.i("TEST", "ABORT!")
                DelayedShutdown.stopped = true
            }
        }
    }

    private class DelayedShutdown(
        context: Context?,
        var delay: Int,
        private val applicationContext: Context? = context?.applicationContext
    ): Thread() {
        companion object {
            var stopped = false
        }

        override fun run() {
            DelayedShutdown.stopped = false

            val wakelock = (applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "autoshutdown:WAKELOCK").apply { acquire() }
            }

            while(delay > 0) {
                Thread.sleep(1000)
                delay -= 1

                if(DelayedShutdown.stopped) {
                    Log.d("TEST", "ABORT.")
                    break
                }
            }

            if(stopped) {
                (applicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(0)
            }else {
                val action = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    "com.android.internal.intent.action.REQUEST_SHUTDOWN"
                }else {
                    "android.intent.action.ACTION_REQUEST_SHUTDOWN"
                }
                var i = Intent(action)
                i.putExtra("android.intent.extra.KEY_CONFIRM", false)
                i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                applicationContext?.startActivity(i)
            }

            wakelock.release()
        }
    }
}