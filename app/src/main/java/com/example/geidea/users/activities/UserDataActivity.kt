package com.example.geidea.users.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.geidea.databinding.ActivityUserDataBinding
import com.example.geidea.users.repositories.UserRepository
import com.example.geidea.users.services.CounterService
import com.example.mealdb.networkmodel.RetrofitService
import com.example.mealdb.viewmodel.UserVewModel
import com.example.mealdb.viewmodel.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class UserDataActivity : AppCompatActivity() {

    lateinit var activityUserDataBinding: ActivityUserDataBinding
    lateinit var userViewModel: UserVewModel
    lateinit var retrofitService:RetrofitService
    var mService: CounterService? = null
    var mBound = false
    lateinit var chronometer:Chronometer
    lateinit var timer :CountDownTimer
    var userID:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUserDataBinding= ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(activityUserDataBinding.root)
        userID= intent.getIntExtra("ID",1)
        initialize()

    }
    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Intent(this@UserDataActivity, CounterService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun initialize() {

        chronometer= activityUserDataBinding.cmeter


        retrofitService= RetrofitService.getInstance()

        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(UserRepository(retrofitService))
        ).get(UserVewModel::class.java)

        userViewModel.getUserData(userID)

        
        userViewModel.userData.observe(this, Observer {
            Log.d("Fragment",it.data.toString())

            activityUserDataBinding.tvEmail.text=it.data.email
            activityUserDataBinding.tvFname.text=it.data.first_name
            activityUserDataBinding.tvLname.text=it.data.last_name
            Glide.with(this)
                .load(it.data.avatar)
                .into(activityUserDataBinding.ivProfilePic)
        })

        chronometer.base= SystemClock.elapsedRealtime()

        timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
               // Log.d("Fragment",millisUntilFinished.toString())
                val elapsedMillis : Long = (SystemClock.elapsedRealtime() - chronometer.getBase())
                val hours = (elapsedMillis / 3600000).toInt()
                val minutes = (elapsedMillis - hours * 3600000).toInt() / 60000
                val seconds = (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000

                Log.d("Fragment",seconds.toString())


            }

            override fun onFinish(){
                chronometer.stop()
                finish()
            }
        }
        chronometer.start()
        CoroutineScope(Dispatchers.IO).launch { startTimer()}

    }

    suspend fun startTimer() {
        delay(1000)
        timer.start()
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as CounterService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}