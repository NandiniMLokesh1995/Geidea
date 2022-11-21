package com.example.mealdb.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geidea.users.Entities.UserData
import com.example.geidea.users.Entities.UserEntity
import com.example.geidea.users.Entities.UserList
import com.example.geidea.users.dao.UserDao
import com.example.geidea.users.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserVewModel(val repository: UserRepository) : ViewModel() {

    var userList = MutableLiveData<UserEntity>()
    var userData = MutableLiveData<UserData>()
    var userLst = MutableLiveData<List<UserList>>()
    var user = MutableLiveData<UserList>()




    var TAG = "UserVewModel"


    fun getUserList() {
        CoroutineScope(Dispatchers.IO).launch {
            /***
             * Pager count value is fixed we can add pagination here instead
             */
            val response: UserEntity = repository.getAllUsers(20)
            Log.d(TAG, "UserData: " + response.data)
            if (!response.data.isEmpty()) {
                userList.postValue(response)
            }
        }
    }

    fun getUserData(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response: UserData = repository.getUserData(id)
            Log.d(TAG, "UserData: " + response.data)
            if (response !== null) {
                userData.postValue(response)
            }

        }
    }

    fun getRoomUserData(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response: UserList = repository.getUserById(id)
            Log.d(TAG, "UserData: " + response)
            if (response !== null) {
                user.postValue(response)
            }

        }
    }

    fun insertUserData(userList: List<UserList>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (repository.getItemCount() == 0) {
                repository.inertAllUsers(userList)
            }

        }
    }
    fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            if (repository.getItemCount() > 0) {
                var response:List<UserList> = repository.getAllUsers()
                Log.d(TAG, "getAllUsers: " + response)

                if(!response.isEmpty()){
                    userLst.postValue(response)
                }
            }

        }
    }
}


