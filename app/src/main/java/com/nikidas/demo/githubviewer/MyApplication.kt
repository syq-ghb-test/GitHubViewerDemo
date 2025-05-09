package com.nikidas.demo.githubviewer

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: MyApplication
        fun getInstance(): MyApplication = instance
    }
} 