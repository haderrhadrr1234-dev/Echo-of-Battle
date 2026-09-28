package com.example

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

object AppGlobals {
    var context: Context? = null
}

class GameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGlobals.context = applicationContext
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            android.util.Log.w("GameApplication", "FirebaseApp init handled gracefully: ${e.message}")
        }
    }
}
