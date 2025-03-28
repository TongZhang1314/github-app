package com.example.githubapp

import android.app.Application
import android.content.Context

class App: Application() {
    companion object {
        private var context: Context? = null

        // 提供全局的 Context 获取方法
        fun getAppContext(): Context {
            return context!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext // 初始化全局 Context
    }
}