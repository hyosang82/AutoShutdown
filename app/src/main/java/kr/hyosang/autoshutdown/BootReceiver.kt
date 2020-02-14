package kr.hyosang.autoshutdown

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(Intent.ACTION_BOOT_COMPLETED == p1?.action) {
            Log.i("TEST", "BOOT_COMPLETED")
            p0?.startService(Intent(p0, PowerMonService::class.java))
        }
    }
}