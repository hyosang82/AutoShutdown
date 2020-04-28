package kr.hyosang.autoshutdown

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPref {
    private var pref: SharedPreferences

    var delaySec: Int
    get() { return pref.getInt(KEY_DELAY_SEC, 30) }
    set(value) {
        with(pref.edit()) {
            putInt(KEY_DELAY_SEC, value)
            commit()
        }
    }

    companion object {
        private const val KEY_DELAY_SEC = "delay_sec"

        private lateinit var _instance: AppPref

        val instance: AppPref
        get() { return _instance }

        fun init(context: Context) {
            _instance = AppPref(context)
        }
    }

    constructor(context: Context) {
        pref = context.getSharedPreferences("autoshutdown", Context.MODE_PRIVATE)
    }


}