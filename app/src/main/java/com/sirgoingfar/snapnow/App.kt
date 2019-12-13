package com.sirgoingfar.snapnow

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}