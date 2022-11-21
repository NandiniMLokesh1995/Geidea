package com.example.geidea.users.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class UserEntity(

    @PrimaryKey
    @ColumnInfo(name = "page")
    var page: Int,
    @ColumnInfo(name = "per_page")
    var per_page: Int,
    @ColumnInfo(name = "data")
    var data: List<UserList>
)
@Entity(tableName = "user")
data class UserList(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "first_name")
    var first_name: String,
    @ColumnInfo(name = "last_name")
    var last_name: String,
    @ColumnInfo(name = "avatar")
    var avatar: String
)

data class UserData(@ColumnInfo(name = "data")
                    var data: UserList)