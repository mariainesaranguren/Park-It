package com.example.mariainesaranguren.parkit

import com.parse.Parse
import android.app.Application


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(this)
    }
}