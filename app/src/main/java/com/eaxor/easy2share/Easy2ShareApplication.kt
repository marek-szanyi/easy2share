package com.eaxor.easy2share

import android.app.Application
import com.eaxor.easy2share.di.AppContainer
import com.eaxor.easy2share.di.DefaultAppContainer

/**
 * Application entry point.
 *
 * Owns the [AppContainer] for the process lifetime, giving the composition root
 * (the Activity) a single place to obtain wired-up dependencies.
 */
class Easy2ShareApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}

