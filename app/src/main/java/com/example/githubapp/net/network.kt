package com.example.githubapp.net

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class GitHubSearchResponse(
    val items: List<GitHubRepo>
)

data class GitHubRepo(
    val id: Int,
    val name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?,
    @SerializedName("html_url")
    val url: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

// 用户数据模型
data class GitHubUser(
    val login: String,
    val avatar_url: String,
    val name: String?,
    @SerializedName("public_repos") val repos: Int
)

data class GitHubIssueRequest(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("labels") val labels: List<String> = emptyList()
)

data class GitHubIssueResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("html_url") val url: String
)

// API 接口定义
interface GitHubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): GitHubSearchResponse

    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") credentials: String,
        @Header("Accept") accept: String = "application/vnd.github+json"
    ): GitHubUser

    @GET("user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") token: String,
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc",
        @Query("per_page") perPage: Int = 20
    ): List<GitHubRepo>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") token: String,
        @Body issueRequest: GitHubIssueRequest
    ): GitHubIssueResponse
}

// Retrofit 实例
object RetrofitClient {
    private const val BASE_URL = "https://api.github.com/"

    val instance: GitHubApiService by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApiService::class.java)
    }
}