package com.katiacammisa.mycar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.katiacammisa.mycar.auth.AuthScreen
import com.katiacammisa.mycar.navigation.BottomBar
import com.katiacammisa.mycar.navigation.NavHostComposable
import com.katiacammisa.mycar.notifications.NotificationOnboardingScreen
import com.katiacammisa.mycar.ui.theme.MyCarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val appPreferences by lazy {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCarTheme {
                var hasSeenNotificationOnboarding by rememberSaveable {
                    mutableStateOf(hasSeenNotificationOnboarding())
                }
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }

                if (!hasSeenNotificationOnboarding) {
                    NotificationOnboardingScreen(
                        onFinished = {
                            markNotificationOnboardingSeen()
                            hasSeenNotificationOnboarding = true
                        },
                    )
                } else if (isAuthenticated) {
                    val navController = rememberNavController()
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            BottomBar(navController::navigate)
                        }
                    ) { innerPadding ->
                        NavHostComposable(innerPadding, navController)
                    }
                } else {
                    AuthScreen(onAuthenticated = { isAuthenticated = true })
                }
            }
        }
    }

    fun shouldRequestNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun hasSeenNotificationOnboarding(): Boolean {
        return appPreferences.getBoolean(HAS_SEEN_NOTIFICATION_ONBOARDING, false)
    }

    private fun markNotificationOnboardingSeen() {
        appPreferences.edit().putBoolean(HAS_SEEN_NOTIFICATION_ONBOARDING, true).apply()
    }

    companion object {
        private const val PREFS_NAME = "mycars_prefs"
        private const val HAS_SEEN_NOTIFICATION_ONBOARDING = "has_seen_notification_onboarding"
    }
}
