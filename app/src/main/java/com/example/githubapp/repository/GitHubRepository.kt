package com.example.githubapp.repository

import com.example.githubapp.App
import com.example.githubapp.Topic
import com.example.githubapp.net.GitHubIssueRequest
import com.example.githubapp.net.GitHubIssueResponse
import com.example.githubapp.net.GitHubRepo
import com.example.githubapp.net.GitHubUser
import com.example.githubapp.net.RetrofitClient

// 令牌token
const val credentials = "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"

// 数据仓库
class GitHubRepository {
    private val apiService = RetrofitClient.instance

    suspend fun searchRepositories(query: String): List<GitHubRepo> {
        return apiService.searchRepositories(query).items
    }

    suspend fun getHotRepositories(): List<GitHubRepo> {
        return apiService.searchRepositories(query = "language:kotlin").items
    }

    // 趋势话题由于GitHub API限制，暂时保留模拟数据
    fun getTrendingTopics(): List<Topic> {
        return listOf(
            Topic(1, "Kotlin 协程最佳实践", 980, "+32%"),
            Topic(2, "Compose 性能优化", 1200, "+45%"),
            // ...其他模拟数据
        )
    }
}

// 认证Repository
class AuthRepository {
    private val apiService = RetrofitClient.instance
    private val prefsManager = PrefsManager(App.getAppContext())

    suspend fun login(username: String, password: String): Result<GitHubUser> {
        return try {
            val user = apiService.getAuthenticatedUser("Bearer $credentials")
            // 保存到 SharedPreferences
            prefsManager.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRepos(): Result<List<GitHubRepo>> {
        return try {
            val repos = apiService.getUserRepositories("Bearer $credentials")
            Result.success(repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 从缓存获取用户
    fun getCachedUser(): GitHubUser? {
        return prefsManager.getCachedUser()
    }

    // 从缓存获取用户
    fun clearCachedUser() {
        return prefsManager.clearUser()
    }
}

class IssueRepository {
    private val apiService = RetrofitClient.instance

    suspend fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String
    ): Result<GitHubIssueResponse> {
        return try {
            val response = apiService.createIssue(
                owner = owner,
                repo = repo,
                token = "token $credentials",
                issueRequest = GitHubIssueRequest(title, body)
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}