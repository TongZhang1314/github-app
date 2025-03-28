package com.example.githubapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.net.GitHubRepo
import com.example.githubapp.net.GitHubUser
import com.example.githubapp.repository.AuthRepository
import kotlinx.coroutines.launch
// 用户认证状态管理
class UserViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var userState by mutableStateOf<GitHubUser?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(username: String, password: String) {
        if (username != "HSBC" || password != "123456") {
            errorMessage = "账号或密码错误,请查看README"
            userState = null
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.login(username, password)
            isLoading = false
            when {
                result.isSuccess -> {
                    userState = result.getOrNull()
                    errorMessage = null
                }
                result.isFailure -> {
                    errorMessage = "登录失败: ${result.exceptionOrNull()?.message}"
                    userState = null
                }
            }
        }
    }

    fun reloadUser() {
        viewModelScope.launch {
            try {
                // 从本地缓存或重新请求数据
                val user = repository.getCachedUser()
                userState = user
            } catch (e: Exception) {
                Log.e("UserViewModel", "Reload user failed", e)
            }
        }
    }

    fun logout() {
        userState = null
        repository.clearCachedUser()
    }

    private val _repoState = mutableStateOf<UiState<List<GitHubRepo>>>(UiState.Idle)
    val repoState: State<UiState<List<GitHubRepo>>> = _repoState

    fun loadRepositories() {
        viewModelScope.launch {
            _repoState.value = UiState.Loading
            val result = repository.getUserRepos()
            when {
                result.isSuccess -> {
                    _repoState.value = if (result.getOrThrow().isNotEmpty()) {
                        UiState.Success(result.getOrThrow())
                    } else {
                        UiState.Empty
                    }
                }
                result.isFailure -> {
                    _repoState.value = UiState.Error(result.exceptionOrNull()?.message ?: "未知错误")
                }
            }
        }
    }
}