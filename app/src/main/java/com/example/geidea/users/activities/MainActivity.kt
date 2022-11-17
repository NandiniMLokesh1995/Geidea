package com.example.geidea.users.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.geidea.databinding.ActivityMainBinding
import com.example.geidea.users.Entities.UserList
import com.example.geidea.users.adapters.UserListAdpter
import com.example.geidea.users.interfaces.ClickListener
import com.example.geidea.users.repositories.UserRepository
import com.example.mealdb.networkmodel.RetrofitService
import com.example.mealdb.viewmodel.UserVewModel
import com.example.mealdb.viewmodel.UserViewModelFactory


class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var userViewModel: UserVewModel
    lateinit var retrofitService: RetrofitService
    lateinit var reclerView: RecyclerView
    lateinit var userListAdpter: UserListAdpter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initialize()
    }

    private fun initialize() {

        retrofitService = RetrofitService.getInstance()
        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(UserRepository(retrofitService))
        ).get(UserVewModel::class.java)

        userListAdpter = UserListAdpter()
        userViewModel.getUserList()

        reclerView = activityMainBinding.rvUser
        reclerView.adapter = userListAdpter


        userViewModel.userList.observe(this, Observer {
            userListAdpter.setData(it.data)
            userListAdpter.notifyDataSetChanged()

        })

    }

    override fun onResume() {
        super.onResume()
        Log.d("Main Activity", "OnRestart")
    }

}
