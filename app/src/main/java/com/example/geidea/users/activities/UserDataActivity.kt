package com.example.geidea.users.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.Network
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.geidea.databinding.ActivityUserDataBinding
import com.example.geidea.users.databases.UserDatabase
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
    lateinit var chronometer:Chronometer
    lateinit var timer :CountDownTimer
    var userID:Int = 0
    lateinit var userDatabase:UserDatabase
    lateinit var connectivityManager :ConnectivityManager
    lateinit var myNetworkCallback: ConnectivityManager.NetworkCallback
    val TAG="UserDataActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUserDataBinding= ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(activityUserDataBinding.root)
        // start to LocalService
        val intent= Intent(this@UserDataActivity, CounterService::class.java)
        startService(intent)
        userID= intent.getIntExtra("ID",1)
        initialize()
        isNetworkConnected()
    }
    override fun onStart() {
        super.onStart()
    }

    private fun initialize() {

        chronometer= activityUserDataBinding.cmeter


        retrofitService= RetrofitService.getInstance()
        userDatabase= UserDatabase.getInstance(this)

        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(UserRepository(retrofitService,userDatabase.userDao()))
        ).get(UserVewModel::class.java)


        
        userViewModel.userData.observe(this, Observer {
            Log.d("Fragment",it.data.toString())

            activityUserDataBinding.tvEmail.text=it.data.email
            activityUserDataBinding.tvFname.text=it.data.first_name
            activityUserDataBinding.tvLname.text=it.data.last_name
            Glide.with(this)
                .load(it.data.avatar)
                .into(activityUserDataBinding.ivProfilePic)
        })

        userViewModel.user.observe(this, Observer {

            activityUserDataBinding.tvEmail.text=it.email
            activityUserDataBinding.tvFname.text=it.first_name
            activityUserDataBinding.tvLname.text=it.last_name
            Glide.with(this)
                .load(it.avatar)
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


    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun isNetworkConnected() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager=getSystemService(ConnectivityManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                myNetworkCallback=
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network : Network) {
                            Log.e(TAG, "The default network is now: " + network)
                            Toast.makeText(applicationContext,"onAvailable", Toast.LENGTH_SHORT).show()
                            userViewModel.getUserData(userID)

                        }

                        override fun onLost(network : Network) {
                            Toast.makeText(applicationContext,"No Lost", Toast.LENGTH_SHORT).show()
                            userViewModel.getRoomUserData(userID)
                            // Log.e(TAG, "The application no longer has a default network. The last default network was " + network)

                        }

                        override fun onUnavailable() {
                            super.onUnavailable()
                        }

                    }
                connectivityManager.registerDefaultNetworkCallback(myNetworkCallback)
            }

        }
    }
    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(myNetworkCallback)
    }
}