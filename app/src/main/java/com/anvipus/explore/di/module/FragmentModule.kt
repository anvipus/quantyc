package com.anvipus.explore.di.module

import com.anvipus.explore.ui.auth.LoginFragment
import com.anvipus.explore.ui.auth.RegisterFragment
import com.anvipus.explore.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun main(): MainFragment

    @ContributesAndroidInjector
    abstract fun login(): LoginFragment

    @ContributesAndroidInjector
    abstract fun register(): RegisterFragment

}