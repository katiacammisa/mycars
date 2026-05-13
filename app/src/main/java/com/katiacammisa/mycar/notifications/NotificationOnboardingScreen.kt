package com.katiacammisa.mycar.notifications

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.katiacammisa.mycar.MainActivity

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationOnboardingScreen(
    onFinished: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findMainActivity()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Stay up to date")
        Text(
            text = "Enable notifications to get app reminders and outside updates.",
            modifier = Modifier.padding(top = 8.dp),
        )

        Button(
            onClick = {
                if (activity?.shouldRequestNotificationPermission() == true) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onFinished()
                }
            },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
        ) {
            Text("Allow notifications")
        }

        OutlinedButton(
            onClick = onFinished,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
        ) {
            Text("Not now")
        }
    }
}

private tailrec fun Context.findMainActivity(): MainActivity? {
    return when (this) {
        is MainActivity -> this
        is ContextWrapper -> baseContext.findMainActivity()
        else -> null
    }
}
