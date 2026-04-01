package com.katiacammisa.mycar.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormsViewModel @Inject constructor(): ViewModel() {

    private var _addedCars = MutableStateFlow(listOf<CarSummaryUi>())
    val addedCars = _addedCars.asStateFlow()

    private var _addedActivities = MutableStateFlow(listOf<ActivityUi>())
    val addedActivities = _addedActivities.asStateFlow()

    fun addCar(car: CarSummaryUi) {
        viewModelScope.launch {
            _addedCars.emit(_addedCars.value + car)
        }
    }

    fun addActivity(activity: ActivityUi) {
        viewModelScope.launch {
            _addedActivities.emit(_addedActivities.value + activity)
        }
    }
}