package com.example.geidea.users.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Objects


class CounterService : Service() {

    private val LOG_TAG = "BoundService"
    private lateinit var timer:CountDownTimer
    private val mBinder: IBinder = LocalBinder()
    var Counter:String=""


    override fun onCreate() {
        super.onCreate()
        Log.v(LOG_TAG, "in onCreate")
        var runTime:Long=3600000L
            //1hr=3600000ms
       startTimer(runTime)

    }

    private fun startTimer(time: Long) {
         timer = object: CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = (millisUntilFinished / 3600000).toInt()
                val minutes = (millisUntilFinished - hours * 3600000).toInt() / 60000
                val seconds = (millisUntilFinished - hours * 3600000 - minutes * 60000) / 1000
                Counter= "$hours:$minutes:$seconds"

                Log.v(LOG_TAG, seconds.toString())
                val intent = Intent("CounterUpdate")
                intent.putExtra("count", Counter)
                LocalBroadcastManager.getInstance(this@CounterService).sendBroadcast(intent)
            }

            override fun onFinish() {
                startTimer(time)

            }
        }
        timer.start()

    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.v(LOG_TAG, "in onBind")
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.v(LOG_TAG, "in onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.v(LOG_TAG, "in onUnbind")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        Log.v(LOG_TAG, "in onDestroy")

    }

inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): CounterService {
            return this@CounterService
        }
    }
}