package com.example.geidea.users.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.*
import android.util.Log
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
    lateinit var userDatabase:UserDatabase
    lateinit var connectivityManager :ConnectivityManager
    lateinit var myNetworkCallback: ConnectivityManager.NetworkCallback
    lateinit var requestOptions :RequestOptions
    val TAG="UserDataActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUserDataBinding= ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(activityUserDataBinding.root)
        // start to LocalService

        var userID= intent.getIntExtra("ID",1)
        val intent= Intent(this@UserDataActivity, CounterService::class.java)
        startService(intent)
        Log.d(TAG, "onCreate: $userID")
        initialize(userID)
        if(isNetworkConnected()){
            Toast.makeText(applicationContext,"Network Available",Toast.LENGTH_SHORT).show()
            userViewModel.getUserData(userID)

        }else{
            Toast.makeText(applicationContext,"Offline",Toast.LENGTH_SHORT).show()
            userViewModel.getRoomUserData(userID)

        }
        networkChange(userID)


    }

    private fun initialize(userID: Int) {

        chronometer= activityUserDataBinding.cmeter


        retrofitService= RetrofitService.getInstance()
        userDatabase= UserDatabase.getInstance(this)

        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(UserRepository(retrofitService,userDatabase.userDao()))
        ).get(UserVewModel::class.java)

        requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

        userViewModel.userData.observe(this, Observer {
            //Log.d("Fragment",it.data.toString())

            activityUserDataBinding.tvEmail.text=it.data.email
            activityUserDataBinding.tvFname.text=it.data.first_name
            activityUserDataBinding.tvLname.text=it.data.last_name
            Glide.with(this)
                .load(it.data.avatar)
                .apply(requestOptions)
                .into(activityUserDataBinding.ivProfilePic)
        })

        userViewModel.user.observe(this, Observer {

            activityUserDataBinding.tvEmail.text=it.email
            activityUserDataBinding.tvFname.text=it.first_name
            activityUserDataBinding.tvLname.text=it.last_name
            Glide.with(this)
                .load(it.avatar)
                .apply(requestOptions)
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

    private fun networkChange(userID:Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager=getSystemService(ConnectivityManager::class.java)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                myNetworkCallback=
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network : Network) {
                            Toast.makeText(applicationContext,"Back Online", Toast.LENGTH_SHORT).show()
                            userViewModel.getUserData(userID)

                        }

                        override fun onLost(network : Network) {
                            Toast.makeText(applicationContext,"Network Lost", Toast.LENGTH_SHORT).show()
                                userViewModel.getRoomUserData(userID)

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

    private fun isNetworkConnected():Boolean{
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val actNetwork      = connectivityManager.activeNetwork ?: return false
            val actNetworkCapabilities = connectivityManager.getNetworkCapabilities(actNetwork) ?: return false
            return when {
                actNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}