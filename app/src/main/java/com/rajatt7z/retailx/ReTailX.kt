package com.rajatt7z.retailx

import android.app.Application
import com.google.android.material.color.DynamicColors

class ReTailX : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}