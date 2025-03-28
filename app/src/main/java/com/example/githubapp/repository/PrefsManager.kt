package com.example.githubapp.repository

import android.content.Context
import android.util.Log
import com.example.githubapp.net.GitHubUser
import com.google.gson.Gson

class PrefsManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 保存用户对象
    fun saveUser(user: GitHubUser) {
        val userJson = gson.toJson(user)
        Log.e("search", "saveUser->$userJson")
        sharedPref.edit()
            .putString(KEY_USER, userJson)
            .apply()
    }

    // 获取缓存的用户对象
    fun getCachedUser(): GitHubUser? {
        val userJson = sharedPref.getString(KEY_USER, null)
        return try {
            gson.fromJson(userJson, GitHubUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // 清除用户数据
    fun clearUser() {
        sharedPref.edit()
            .remove(KEY_USER)
            .apply()
    }

    companion object {
        private const val KEY_USER = "current_user"
    }
}