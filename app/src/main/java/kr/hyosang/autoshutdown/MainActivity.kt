package kr.hyosang.autoshutdown

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData

import kotlinx.android.synthetic.main.activity_main.*
import kr.hyosang.autoshutdown.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var shutdownDelay = MutableLiveData<String>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this
        binding.lifecycleOwner = this

        setSupportActionBar(toolbar)

        shutdownDelay.value = "${AppPref.instance.delaySec}"

        fab.setOnClickListener { view ->
            shutdownDelay.value?.let {
                val v = Integer.parseInt(it, 10)
                AppPref.instance.delaySec = v
                Snackbar.make(view, "Shutdown delay time set to $v seconds.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }

        startService(Intent(this, PowerMonService::class.java))
    }

}
