package com.merokisab.app

import android.app.Application
import com.merokisab.app.data.database.AppDatabase

class MeroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(this)
    }
}