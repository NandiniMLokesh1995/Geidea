package com.example.geidea.users.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.geidea.users.Entities.UserList
import com.example.geidea.users.dao.UserDao

@Database(entities = [UserList::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object {
        var userDatbase: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            if (userDatbase == null) {
                userDatbase = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java, "user_database"
                ).build()
            }
            return userDatbase as UserDatabase

        }
    }

}