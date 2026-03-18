package com.katiacammisa.mycar.input

import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyCarsEntryScreen(
    modifier: Modifier = Modifier,
    onSaveCar: () -> Unit = {},
    onSaveActivity: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    var selectedMode by rememberSaveable { mutableStateOf(FormMode.CAR) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Add Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Register a new car or log a maintenance activity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                SegmentedButton(
                    selected = selectedMode == FormMode.CAR,
                    onClick = { selectedMode = FormMode.CAR },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AddCircle,
                            contentDescription = null,
                        )
                    },
                    label = { Text("New Car") },
                )
                SegmentedButton(
                    selected = selectedMode == FormMode.ACTIVITY,
                    onClick = { selectedMode = FormMode.ACTIVITY },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AddCircle,
                            contentDescription = null,
                        )
                    },
                    label = { Text("New Activity") },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedMode) {
                FormMode.CAR -> {
                    AddCarForm(
                        onSave = onSaveCar,
                        onCancel = onCancel,
                    )
                }

                FormMode.ACTIVITY -> {
                    AddActivityForm(
                        onSave = onSaveActivity,
                        onCancel = onCancel,
                    )
                }
            }
        }
    }
}

@Composable
fun AddCarForm(
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    var nickname by rememberSaveable { mutableStateOf("") }
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var mileage by rememberSaveable { mutableStateOf("") }
    var vin by rememberSaveable { mutableStateOf("") }
    var insurance by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FormHeader(
                title = "New Car",
                subtitle = "Save the basic information for a family vehicle",
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nickname") },
                placeholder = { Text("Mom's SUV") },
                singleLine = true,
            )

            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Make") },
                placeholder = { Text("Toyota") },
                singleLine = true,
            )

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Model") },
                placeholder = { Text("Corolla Cross") },
                singleLine = true,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Year") },
                    placeholder = { Text("2022") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Plate") },
                    placeholder = { Text("AB123CD") },
                    singleLine = true,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Color") },
                    placeholder = { Text("Silver") },
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
                value = vin,
                onValueChange = { vin = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("VIN") },
                placeholder = { Text("Optional") },
                singleLine = true,
            )

            OutlinedTextField(
                value = insurance,
                onValueChange = { insurance = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Insurance / Policy") },
                placeholder = { Text("Company or policy number") },
                singleLine = true,
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes") },
                placeholder = { Text("Any useful details about the vehicle") },
                minLines = 4,
            )

            FormActions(
                saveText = "Save Car",
                onSave = onSave,
                onCancel = onCancel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityForm(
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    val cars = remember {
        listOf("Dad's SUV", "Mom's City Car", "Family Van")
    }
    val activityTypes = remember {
        listOf(
            "Oil Change",
            "Tire Change",
            "Brake Service",
            "Battery Check",
            "General Inspection",
            "Other",
        )
    }

    var selectedCar by rememberSaveable { mutableStateOf(cars.first()) }
    var selectedType by rememberSaveable { mutableStateOf(activityTypes.first()) }
    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var mileage by rememberSaveable { mutableStateOf("") }
    var cost by rememberSaveable { mutableStateOf("") }
    var workshop by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    var carExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FormHeader(
                title = "New Activity",
                subtitle = "Register a maintenance task or service for a vehicle",
            )

            ExposedDropdownMenuBox(
                expanded = carExpanded,
                onExpandedChange = { carExpanded = !carExpanded },
            ) {
                OutlinedTextField(
                    value = selectedCar,
                    onValueChange = {},
                    readOnly = true,
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Cost") },
                    placeholder = { Text("$ 120000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = workshop,
                    onValueChange = { workshop = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Workshop") },
                    placeholder = { Text("Quick Service") },
                    singleLine = true,
                )
            }

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
                onSave = onSave,
                onCancel = onCancel,
            )
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
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilledTonalButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text("Cancel")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text(saveText)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FamilyCarsEntryScreenPreview() {
    MaterialTheme {
        FamilyCarsEntryScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun AddCarFormPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AddCarForm()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddActivityFormPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AddActivityForm()
        }
    }
}