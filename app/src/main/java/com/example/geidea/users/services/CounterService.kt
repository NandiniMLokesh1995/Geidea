package com.example.geidea.users.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class CounterService : Service() {

    private val LOG_TAG = "BoundService"
    private val mBinder: IBinder = LocalBinder()
    var Counter:String=""

    override fun onCreate() {
        super.onCreate()
        Log.v(LOG_TAG, "in onCreate")
        var runTime:Long=3600000L
        val timer = object: CountDownTimer(runTime, 1000) {
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