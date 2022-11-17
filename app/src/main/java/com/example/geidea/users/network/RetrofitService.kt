package com.example.mealdb.networkmodel
import com.example.geidea.users.Entities.UserData
import com.example.geidea.users.Entities.UserEntity
import com.example.geidea.users.Entities.UserList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {


    @GET("users/")
    suspend fun getUsers(@Query("per_page") perPage:Int) : UserEntity

    @GET("users/{id}")
    suspend fun getUsersData(@Path("id") id:Int) : UserData


    companion object {
        var baseUrl ="https://reqres.in/api/"

        var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}