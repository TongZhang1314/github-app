package com.example.githubapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.net.GitHubIssueResponse
import com.example.githubapp.repository.IssueRepository
import kotlinx.coroutines.launch

class IssueViewModel(
    private val repository: IssueRepository
) : ViewModel() {
    var selectedRepo by mutableStateOf<Pair<String, String>?>(null)
        private set

    var showDialog by mutableStateOf(false)

    val issueState = mutableStateOf<UiState<GitHubIssueResponse>?>(null)

    // 新增成功状态标识
    var showSuccessToast by mutableStateOf(false)

    fun showCreateDialog(repoOwner: String, repoName: String) {
        selectedRepo = repoOwner to repoName
        showDialog = true
    }

    fun createIssue(title: String, content: String) {
        viewModelScope.launch {
            selectedRepo?.let { (owner, repo) ->
                issueState.value = UiState.Loading
                val result = repository.createIssue(owner, repo, title, content)

                issueState.value = when {
                    result.isSuccess -> {
                        UiState.Success(result.getOrThrow())
                    }
                    result.isFailure -> UiState.Error(result.exceptionOrNull()?.message ?: "提交失败")
                    else -> UiState.Empty
                }

                if (result.isSuccess) {
                    showDialog = false // 关闭弹窗
                    showSuccessToast = true // 触发Toast显示
                }
            }
        }
    }

    fun resetToastState() {
        showSuccessToast = false
    }
}