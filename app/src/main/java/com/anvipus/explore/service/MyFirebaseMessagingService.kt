package com.anvipus.explore.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import timber.log.Timber

class MyFirebaseMessagingService  : FirebaseMessagingService() {
    override fun onCreate() {
        Timber.d("PUSH onCreate")
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

    }
    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
    }
}