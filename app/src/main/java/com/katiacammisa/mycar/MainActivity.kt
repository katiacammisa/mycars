package com.katiacammisa.mycar

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import com.katiacammisa.mycar.ui.theme.MyCarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCarTheme {
                var isAuthenticated by rememberSaveable { mutableStateOf(false) }

                if (isAuthenticated) {
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
}
