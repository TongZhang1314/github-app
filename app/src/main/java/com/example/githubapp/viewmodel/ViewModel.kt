package com.example.githubapp.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.githubapp.repository.AuthRepository
import com.example.githubapp.repository.GitHubRepository
import com.example.githubapp.repository.IssueRepository

// UI 状态密封类
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    object Empty : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class SearchViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            repository = GitHubRepository()
        ) as T
    }
}


@Composable
fun provideUserViewModel(): UserViewModel {
    val repository = remember { AuthRepository() }
    return viewModel { UserViewModel(repository) }
}

@Composable
fun provideIssueViewModel(): IssueViewModel {
    val repository = remember {IssueRepository()}
    return viewModel { IssueViewModel(repository) }
}