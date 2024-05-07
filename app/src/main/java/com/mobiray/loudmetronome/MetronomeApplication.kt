package com.mobiray.loudmetronome

import android.app.Application
import com.mobiray.loudmetronome.di.ApplicationComponent
import com.mobiray.loudmetronome.di.DaggerApplicationComponent

class MetronomeApplication: Application() {

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}