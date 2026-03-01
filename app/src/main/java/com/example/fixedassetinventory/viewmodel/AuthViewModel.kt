package com.example.fixedassetinventory.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel // DAGDAG
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope // DAGDAG
import com.example.fixedassetinventory.data.dao.UserDao // DAGDAG
import com.example.fixedassetinventory.data.entity.User
import com.example.fixedassetinventory.utils.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")

    var errorMessage by mutableStateOf<String?>("")
    var isLoading by mutableStateOf(false)



    fun registerUser(onSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "All fields are required"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true
                val hashedPassword = SecurityUtils.hashPassword(password.trim())
                val newUser = User(
                    username = username.trim(),
                    passwordHash = hashedPassword
                )

                userDao.register(newUser)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = null
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Username already Exist!"
                }
            }
        }
    }
    fun login(onSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Please enter both username and password"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true

                val trimmedPassword = password.trim()
                val inputHash = SecurityUtils.hashPassword(trimmedPassword)

                val user = userDao.login(username.trim(), inputHash)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (user != null) {
                        errorMessage = null
                        onSuccess()
                    }else {
                        errorMessage = "Invalid username or password"
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    android.util.Log.e("AUTH_ERROR", "Login failed", e)
                    errorMessage = "Debug: ${e.localizedMessage}"
                }
            }
        }
    }

    fun resetState() {
        username = ""
        password = ""
        errorMessage = null
        isLoading = false
    }

    companion object {
        fun provideFactory(userDao: UserDao): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userDao) as T
            }
        }
    }
}