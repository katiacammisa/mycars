package com.katiacammisa.mycar.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.katiacammisa.mycar.home.data.ActivityType
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityForm(
    modifier: Modifier = Modifier,
    onSave: (ActivityUi) -> Unit = {},
    onCancel: () -> Unit = {},
) {
    val viewModel = hiltViewModel<FormsViewModel>()

    val addedCars by viewModel.addedCars.collectAsStateWithLifecycle()
    val addedActivities by viewModel.addedActivities.collectAsStateWithLifecycle()

    val tabTitles = remember {
        listOf("New Activity", "New Car", "Saved")
    }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }

            when (selectedTab) {
                0 -> AddActivityTab(
                    cars = addedCars.map { it.nickname },
                    onSave = {
                        viewModel.addActivity(it)
                        onSave(it)
                        selectedTab = 2
                    },
                    onCancel = onCancel,
                )

                1 -> AddCarTab(
                    onSave = {
                        viewModel.addCar(it)
                        selectedTab = 2
                    },
                    onCancel = onCancel,
                )

                else -> SavedItemsTab(
                    addedCars = addedCars,
                    addedActivities = addedActivities,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActivityTab(
    cars: List<String>,
    onSave: (ActivityUi) -> Unit,
    onCancel: () -> Unit,
) {
    val activityTypes = remember {
        ActivityType.entries.map { it.displayName }
    }

    var selectedCar by rememberSaveable(cars) { mutableStateOf(cars.firstOrNull().orEmpty()) }
    var selectedType by rememberSaveable { mutableStateOf(activityTypes.first()) }
    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var mileage by rememberSaveable { mutableStateOf("") }
    var workshop by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    var carExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val canSave = cars.isNotEmpty() && title.isNotBlank() && date.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormHeader(
            title = "New Activity",
            subtitle = "Register a maintenance task or service for a vehicle",
        )

        ExposedDropdownMenuBox(
            expanded = carExpanded,
            onExpandedChange = {
                if (cars.isNotEmpty()) {
                    carExpanded = !carExpanded
                }
            },
        ) {
            OutlinedTextField(
                value = if (cars.isEmpty()) "Add a car first" else selectedCar,
                onValueChange = {},
                readOnly = true,
                enabled = cars.isNotEmpty(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                label = { Text("Car") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = carExpanded)
                },
            )
            ExposedDropdownMenu(
                expanded = carExpanded,
                onDismissRequest = { carExpanded = false },
            ) {
                cars.forEach { car ->
                    DropdownMenuItem(
                        text = { Text(car) },
                        onClick = {
                            selectedCar = car
                            carExpanded = false
                        },
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded },
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                label = { Text("Activity Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                },
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false },
            ) {
                activityTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            typeExpanded = false
                        },
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Title") },
            placeholder = { Text("Changed front tires") },
            singleLine = true,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                modifier = Modifier.weight(1f),
                label = { Text("Date") },
                placeholder = { Text("18/03/2026") },
                singleLine = true,
            )

            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                modifier = Modifier.weight(1f),
                label = { Text("Mileage") },
                placeholder = { Text("42180") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        OutlinedTextField(
            value = workshop,
            onValueChange = { workshop = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Workshop") },
            placeholder = { Text("Quick Service") },
            singleLine = true,
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Notes") },
            placeholder = { Text("Extra details about the service performed") },
            minLines = 4,
        )

        FormActions(
            saveText = "Save Activity",
            saveEnabled = canSave,
            onSave = {
                onSave(
                    ActivityUi(
                        id = UUID.randomUUID().toString(),
                        carName = selectedCar,
                        title = title,
                        description = notes,
                        date = date,
                        mileage = mileage,
                        type = ActivityType.fromDisplayName(selectedType),
                    ),
                )
            },
            onCancel = onCancel,
        )
    }
}

@Composable
private fun AddCarTab(
    onSave: (CarSummaryUi) -> Unit,
    onCancel: () -> Unit,
) {
    var nickname by rememberSaveable { mutableStateOf("") }
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var mileage by rememberSaveable { mutableStateOf("") }

    val canSave = nickname.isNotBlank() && make.isNotBlank() && model.isNotBlank() && year.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormHeader(
            title = "New Car",
            subtitle = "Create a vehicle to assign future activities",
        )

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nickname") },
            placeholder = { Text("Dad's SUV") },
            singleLine = true,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                modifier = Modifier.weight(1f),
                label = { Text("Make") },
                placeholder = { Text("Toyota") },
                singleLine = true,
            )
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                modifier = Modifier.weight(1f),
                label = { Text("Model") },
                placeholder = { Text("Corolla") },
                singleLine = true,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                modifier = Modifier.weight(1f),
                label = { Text("Year") },
                placeholder = { Text("2020") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                modifier = Modifier.weight(1f),
                label = { Text("Mileage") },
                placeholder = { Text("42180") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        OutlinedTextField(
            value = plate,
            onValueChange = { plate = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Plate") },
            placeholder = { Text("ABC123") },
            singleLine = true,
        )

        FormActions(
            saveText = "Save Car",
            saveEnabled = canSave,
            onSave = {
                onSave(
                    CarSummaryUi(
                        id = UUID.randomUUID().toString(),
                        nickname = nickname,
                        make = make,
                        model = model,
                        year = year.toIntOrNull() ?: 0,
                        plate = plate,
                        mileage = mileage,
                    ),
                )
            },
            onCancel = onCancel,
        )
    }
}

@Composable
private fun SavedItemsTab(
    addedCars: List<CarSummaryUi>,
    addedActivities: List<ActivityUi>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormHeader(
            title = "Saved Records",
            subtitle = "Cars and activities currently in the view model",
        )

        Text(
            text = "Cars (${addedCars.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (addedCars.isEmpty()) {
            Text(
                text = "No cars added yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            addedCars.forEach { car ->
                Text("${car.nickname} - ${car.make} ${car.model} (${car.year})")
            }
        }

        HorizontalDivider()

        Text(
            text = "Activities (${addedActivities.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (addedActivities.isEmpty()) {
            Text(
                text = "No activities added yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            addedActivities.forEach { activity ->
                Text("${activity.date} - ${activity.title} (${activity.carName})")
            }
        }
    }
}

@Composable
private fun FormHeader(
    title: String,
    subtitle: String,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FormActions(
    saveText: String,
    saveEnabled: Boolean = true,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
        ) {
            Text("Cancel")
        }
        Button(
            onClick = onSave,
            enabled = saveEnabled,
            modifier = Modifier.weight(1f),
        ) {
            Text(saveText)
        }
    }
}
