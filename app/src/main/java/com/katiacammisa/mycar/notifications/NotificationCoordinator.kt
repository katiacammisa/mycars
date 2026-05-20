package com.katiacammisa.mycar.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.katiacammisa.mycar.MainActivity
import com.katiacammisa.mycar.R
import com.katiacammisa.mycar.data.GarageRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Singleton
class NotificationCoordinator @Inject constructor(
    private val app: Application,
    private val garageRepository: GarageRepository,
) {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var previousActivityIds: Set<String>? = null

    fun start() {
        createNotificationChannel()
        observeOutsideUpdates()
        observeAppForegroundState()
    }

    private fun observeOutsideUpdates() {
        appScope.launch {
            garageRepository.activities.collectLatest @RequiresPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) { activities ->
                val currentIds = activities.map { it.id }.toSet()
                val previousIdsSnapshot = previousActivityIds
                previousActivityIds = currentIds

                if (previousIdsSnapshot == null) return@collectLatest

                val hasNewUpdate = currentIds.size > previousIdsSnapshot.size &&
                    currentIds.minus(previousIdsSnapshot).isNotEmpty()

                if (hasNewUpdate) {
                    showOutsideUpdateNotification()
                }
            }
        }
    }

    private fun observeAppForegroundState() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                cancelAppClosedNotification()
            }

            override fun onStop(owner: LifecycleOwner) {
                scheduleAppClosedNotification()
            }
        })
    }

    private fun scheduleAppClosedNotification() {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + APP_CLOSED_DELAY_MS,
            appClosedNotificationPendingIntent(),
        )
    }

    private fun cancelAppClosedNotification() {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(appClosedNotificationPendingIntent())
        NotificationManagerCompat.from(app).cancel(APP_CLOSED_NOTIFICATION_ID)
    }

    private fun appClosedNotificationPendingIntent(): PendingIntent {
        val intent = Intent(app, AppClosedNotificationReceiver::class.java)
        return PendingIntent.getBroadcast(
            app,
            APP_CLOSED_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showOutsideUpdateNotification() {
        if (!canPostNotifications(app)) return

        val openAppIntent = Intent(app, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingOpenAppIntent = PendingIntent.getActivity(
            app,
            OUTSIDE_UPDATE_NOTIFICATION_ID,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(app, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle(app.getString(R.string.notification_outside_update_title))
            .setContentText(app.getString(R.string.notification_outside_update_text))
            .setContentIntent(pendingOpenAppIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(app).notify(OUTSIDE_UPDATE_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            app.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = app.getString(R.string.notification_channel_description)
        }

        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "mycars_notifications"
        const val APP_CLOSED_NOTIFICATION_ID = 1001
        const val OUTSIDE_UPDATE_NOTIFICATION_ID = 1002
        private const val APP_CLOSED_DELAY_MS = 3_000L

        fun canPostNotifications(context: Context): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
