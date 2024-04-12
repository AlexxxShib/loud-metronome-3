package com.mobiray.loudmetronome.di

import android.content.Context
import com.alexxxshib.vknewsclient.di.ApplicationScope
import com.mobiray.loudmetronome.soundengine.SoundEngine
import com.mobiray.loudmetronome.soundengine.SoundEngineImpl
import com.mobiray.loudmetronome.soundengine.TapTempoHelper
import dagger.Module
import dagger.Provides

@Module
interface SoundEngineModule {

    companion object {

        @ApplicationScope
        @Provides
        fun provideSoundEngine(
            context: Context
        ): SoundEngine {
            return SoundEngineImpl(
                context = context,
                samplePackIndex = 0
            )
        }

        @ApplicationScope
        @Provides
        fun provideTapTempoHelper(): TapTempoHelper {
            return TapTempoHelper()
        }
    }
}