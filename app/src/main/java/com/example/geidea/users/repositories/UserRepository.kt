package com.example.geidea.users.repositories

import com.example.geidea.users.Entities.UserData
import com.example.geidea.users.Entities.UserEntity
import com.example.geidea.users.Entities.UserList
import com.example.geidea.users.dao.UserDao
import com.example.mealdb.networkmodel.RetrofitService
import kotlinx.coroutines.flow.Flow


class UserRepository(private val retrofitService: RetrofitService, private val uDAO: UserDao) {

    suspend fun getAllUsers(pageCount: Int): UserEntity = retrofitService.getUsers(pageCount)
    suspend fun getUserData(id: Int): UserData = retrofitService.getUsersData(id)
    fun getUserById(id: Int): UserList = uDAO.getUserById(id)
    fun getAllUsers(): List<UserList> = uDAO.getAll()
    fun inertAllUsers(allUsers: List<UserList>) = uDAO.insertAll(allUsers)
    fun getItemCount():Int =uDAO.getItemCount()


}