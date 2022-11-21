package com.example.geidea.users.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.geidea.users.Entities.UserData
import com.example.geidea.users.Entities.UserList;
import kotlinx.coroutines.flow.Flow;

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): List<UserList>
    @Query("SELECT * FROM user WHERE id IN (:uId)")
    fun getUserById(uId:Int): UserList
    @Insert
    fun insertAll(users: List<UserList>)

    @Query("SELECT COUNT(*) FROM user")
    fun getItemCount(): Int
}
