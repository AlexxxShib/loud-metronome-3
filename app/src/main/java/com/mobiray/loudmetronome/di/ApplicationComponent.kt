package com.mobiray.loudmetronome.di

import android.content.Context
import com.alexxxshib.vknewsclient.di.ApplicationScope
import com.mobiray.loudmetronome.presentation.ViewModelFactory
import com.mobiray.loudmetronome.soundengine.SoundEngine
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        ViewModelModule::class,
        SoundEngineModule::class
    ]
)
interface ApplicationComponent {

    fun getViewModelFactory(): ViewModelFactory

    fun getSoundEngine(): SoundEngine

    @Component.Factory
    interface ApplicationComponentFactory {

        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}