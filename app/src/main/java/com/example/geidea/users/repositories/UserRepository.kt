package com.example.geidea.users.repositories

import com.example.geidea.users.Entities.UserData
import com.example.geidea.users.Entities.UserEntity
import com.example.geidea.users.Entities.UserList
import com.example.mealdb.networkmodel.RetrofitService


class UserRepository(private val retrofitService: RetrofitService) {
    suspend fun getAllUsers(pageCount:Int) : UserEntity = retrofitService.getUsers(pageCount)
    suspend fun getUserData(id:Int) : UserData = retrofitService.getUsersData(id)

}