package com.example.geidea.users.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener


class CounterService : Service() {

    private val LOG_TAG = "BoundService"
    private val mBinder: IBinder = LocalBinder()
    var Counter:Long=0L

    override fun onCreate() {
        super.onCreate()
        Log.v(LOG_TAG, "in onCreate")
        val timer = object: CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = (millisUntilFinished / 3600000).toInt()
                val minutes = (millisUntilFinished - hours * 3600000).toInt() / 60000
                val seconds = (millisUntilFinished - hours * 3600000 - minutes * 60000) / 1000
                Log.v(LOG_TAG, seconds.toString())
                Counter=seconds
            }

            override fun onFinish() {}
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
        Log.v(LOG_TAG, "in onDestroy")
    }


inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): CounterService {
            return this@CounterService
        }
    }
}