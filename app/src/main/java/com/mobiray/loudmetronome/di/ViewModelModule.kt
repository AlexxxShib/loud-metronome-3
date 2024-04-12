package com.mobiray.loudmetronome.di

import androidx.lifecycle.ViewModel
import com.alexxxshib.vknewsclient.di.ApplicationScope
import com.alexxxshib.vknewsclient.di.ViewModelKey
import com.mobiray.loudmetronome.presentation.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @ApplicationScope
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel) : ViewModel
}