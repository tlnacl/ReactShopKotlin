package com.tlnacl.reactiveapp

import android.app.Application
import com.tlnacl.reactiveapp.di.AndroidModule
import com.tlnacl.reactiveapp.di.AppComponent
import com.tlnacl.reactiveapp.di.DaggerAppComponent
import timber.log.Timber

/**
 * Created by tomt on 29/05/17.
 */
class AndroidApplication : Application() {

    val appComponent: AppComponent = DaggerAppComponent.builder()
            .androidModule(AndroidModule(this))
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}