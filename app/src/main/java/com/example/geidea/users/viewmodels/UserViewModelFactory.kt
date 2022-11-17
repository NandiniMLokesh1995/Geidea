package com.example.mealdb.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geidea.users.repositories.UserRepository

class UserViewModelFactory(private val repository: UserRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserVewModel::class.java)) {
            return UserVewModel(repository) as T
        }

        throw IllegalArgumentException("Illegal argument Exception")
    }

}