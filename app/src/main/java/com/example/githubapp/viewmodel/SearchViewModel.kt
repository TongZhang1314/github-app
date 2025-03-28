package com.example.githubapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.net.GitHubRepo
import com.example.githubapp.repository.GitHubRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    var searchQuery by mutableStateOf("")
    private var debounceJob: Job? = null

    private val _searchState = mutableStateOf<UiState<List<GitHubRepo>>>(UiState.Idle)
    val searchState: State<UiState<List<GitHubRepo>>> = _searchState

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(500) // 防抖处理
            if (query.isNotBlank()) {
                performSearch(query)
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            try {
                val results = repository.searchRepositories("language:$query")
                _searchState.value = if (results.isNotEmpty()) {
                    UiState.Success(results)
                } else {
                    UiState.Empty
                }
            } catch (e: Exception) {
                _searchState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun retrySearch() {
        if (searchQuery.isNotBlank()) {
            performSearch(searchQuery)
        }
    }
}