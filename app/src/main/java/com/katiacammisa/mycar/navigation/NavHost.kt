package com.katiacammisa.mycar.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.katiacammisa.mycar.home.CarDetailsRoute
import com.katiacammisa.mycar.home.FamilyCarsHomeRoute
import com.katiacammisa.mycar.input.AddActivityForm
import com.katiacammisa.mycar.profile.FamilyProfileScreen

private const val CarDetailsRoutePattern = "carDetails/{carId}"
private const val CarDetailsRoutePrefix = "carDetails"

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun NavHostComposable(innerPadding: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = MyCarScreen.Home.name,
        modifier = Modifier.fillMaxSize().padding(innerPadding)
    ) {
        composable(route = MyCarScreen.Home.name) {
            FamilyCarsHomeRoute(
                onCarClick = { car ->
                    navController.navigate("$CarDetailsRoutePrefix/${car.id}")
                },
            )
        }
        composable(route = MyCarScreen.Add.name) {
            AddActivityForm()
        }
        composable(route = MyCarScreen.Profile.name) {
            FamilyProfileScreen(familyName = "Familia Cammisa")
        }
        composable(
            route = CarDetailsRoutePattern,
            arguments = listOf(navArgument("carId") { type = NavType.StringType }),
        ) {
            CarDetailsRoute(onBack = navController::popBackStack)
        }
    }
}
