package com.dm.mochat.watch.data

import android.app.Application
import com.google.firebase.FirebaseApp

class WatchApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}