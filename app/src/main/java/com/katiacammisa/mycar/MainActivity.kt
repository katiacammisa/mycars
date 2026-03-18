package com.katiacammisa.mycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.katiacammisa.mycar.home.FamilyCarsHomeScreen
import com.katiacammisa.mycar.input.FamilyCarsEntryScreen
import com.katiacammisa.mycar.profile.FamilyProfileScreen
import com.katiacammisa.mycar.ui.theme.MyCarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCarTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    FamilyCarsHomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                    FamilyCarsEntryScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                    FamilyProfileScreen(
                        modifier = Modifier.padding(innerPadding),
                        familyName = "Familia Cammisa"
                    )
                }
            }
        }
    }
}