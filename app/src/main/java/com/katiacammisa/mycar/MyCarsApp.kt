package com.katiacammisa.mycar

import android.app.Application
import com.katiacammisa.mycar.notifications.NotificationCoordinator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyCarsApp: Application() {

    @Inject
    lateinit var notificationCoordinator: NotificationCoordinator

    override fun onCreate() {
        super.onCreate()
        notificationCoordinator.start()
    }
}
