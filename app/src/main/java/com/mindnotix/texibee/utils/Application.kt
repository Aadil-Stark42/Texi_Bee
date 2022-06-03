package com.mindnotix.texibee.utils

import com.mindnotix.texibee.utils.MnxPreferenceManager.initializePreferenceManager
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        activity = getActivity() as Activity?
        val sharedPreferences =
            this.applicationContext.getSharedPreferences(Constant.PREF_NAME, MODE_PRIVATE)
        initializePreferenceManager(sharedPreferences)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    companion object {
        var context: Context? = null
            private set
        private var activity: Activity? = null
        fun getActivity(): Context? {
            return activity
        }
    }
}