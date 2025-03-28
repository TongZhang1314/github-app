package com.example.githubapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.Topic
import com.example.githubapp.net.GitHubRepo
import com.example.githubapp.repository.GitHubRepository
import kotlinx.coroutines.launch

// 主 ViewModel
class MainViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _hotReposState = mutableStateOf<UiState<List<GitHubRepo>>>(UiState.Loading)
    val hotReposState: State<UiState<List<GitHubRepo>>> = _hotReposState

    private val _trendingTopicsState = mutableStateOf<UiState<List<Topic>>>(UiState.Loading)
    val trendingTopicsState: State<UiState<List<Topic>>> = _trendingTopicsState

    init {
        loadHotRepositories()
        loadTrendingTopics()
    }

    private fun loadHotRepositories() {
        viewModelScope.launch {
            try {
                val repos = repository.getHotRepositories()
                _hotReposState.value = UiState.Success(repos)
            } catch (e: Exception) {
                _hotReposState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun loadTrendingTopics() {
        viewModelScope.launch {
            try {
                val topics = repository.getTrendingTopics()
                _trendingTopicsState.value = UiState.Success(topics)
            } catch (e: Exception) {
                _trendingTopicsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}