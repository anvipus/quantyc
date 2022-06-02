package com.anvipus.explore.di.module

import com.anvipus.explore.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity
}