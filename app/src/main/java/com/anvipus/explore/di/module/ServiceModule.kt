package com.anvipus.explore.di.module

import com.anvipus.explore.service.MyFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun pushService(): MyFirebaseMessagingService

}