package com.example.geidea.users.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geidea.databinding.ActivityMainBinding
import com.example.geidea.users.adapters.UserListAdpter
import com.example.geidea.users.repositories.UserRepository
import com.example.mealdb.networkmodel.RetrofitService
import com.example.mealdb.viewmodel.UserVewModel
import com.example.mealdb.viewmodel.UserViewModelFactory
import com.example.geidea.users.databases.UserDatabase


class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var userViewModel: UserVewModel
    lateinit var retrofitService: RetrofitService
    lateinit var reclerView: RecyclerView
    lateinit var userListAdpter: UserListAdpter
    lateinit var mInentService: ServiceBroadCastReceiver
    lateinit var userDatabase:UserDatabase
    lateinit var connectivityManager :ConnectivityManager
    lateinit var myNetworkCallback:NetworkCallback


    val TAG="MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initialize()
        if(isNetworkConnected()){
            Toast.makeText(applicationContext,"Network Available",Toast.LENGTH_SHORT).show()
            userViewModel.getUserList()

        }else{
            Toast.makeText(applicationContext,"Offline",Toast.LENGTH_SHORT).show()
            userViewModel.getAllUsers()

        }
        networkChange()

    }


    private fun initialize() {

        retrofitService = RetrofitService.getInstance()
        userDatabase= UserDatabase.getInstance(this)

        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(UserRepository(retrofitService,userDatabase.userDao()))
        ).get(UserVewModel::class.java)


        userListAdpter = UserListAdpter()

        reclerView = activityMainBinding.rvUser
        reclerView.adapter = userListAdpter

        userViewModel.userList.observe(this, Observer {
            userListAdpter.setData(it.data)
            userListAdpter.notifyDataSetChanged()

            userViewModel.insertUserData(it.data)

        })
        userViewModel.userLst.observe(this, Observer {
            userListAdpter.setData(it)
            userListAdpter.notifyDataSetChanged()

        })
        mInentService=ServiceBroadCastReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mInentService, IntentFilter("CounterUpdate")
        );

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mInentService)
    }

 inner  class ServiceBroadCastReceiver(): BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
           var data= intent?.getStringExtra("count")
            if (data != null) {
                activityMainBinding.tvCounter.text=data

            }
        }

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

    private fun networkChange() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager=getSystemService(ConnectivityManager::class.java)



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                myNetworkCallback=
                    object : ConnectivityManager.NetworkCallback() {

                        override fun onAvailable(network : Network) {
                            //Log.e(TAG, "The default network is now: " + network)
                            Toast.makeText(applicationContext,"Back Online",Toast.LENGTH_SHORT).show()
                            userViewModel.getUserList()

                        }

                        override fun onLost(network : Network) {
                            Toast.makeText(applicationContext,"Network Lost",Toast.LENGTH_SHORT).show()
                            userViewModel.getAllUsers()
                            // Log.e(TAG, "The application no longer has a default network. The last default network was " + network)

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
