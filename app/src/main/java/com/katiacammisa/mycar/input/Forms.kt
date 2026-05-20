package com.katiacammisa.mycar.input

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import com.katiacammisa.mycar.R
import com.katiacammisa.mycar.home.data.ActivityType
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import java.util.UUID
import java.io.File
import java.io.FileOutputStream

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
    val catalogState by viewModel.catalogState.collectAsStateWithLifecycle()

    val tabTitles = remember {
        listOf(
            R.string.form_tab_new_activity,
            R.string.form_tab_new_car,
            R.string.form_tab_saved,
        )
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
                tabTitles.forEachIndexed { index, titleRes ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(stringResource(titleRes)) },
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
                    makes = catalogState.makes,
                    modelsByMake = catalogState.modelsByMake,
                    isCatalogLoading = catalogState.isLoading,
                    catalogError = catalogState.error,
                    onRetryCatalog = viewModel::loadCatalog,
                    onSave = {
                        viewModel.addCar(it)
                        selectedTab = 2
                    },
                    onCancel = onCancel,
                )

                else -> SavedItemsTab(
                    addedCars = addedCars,
                    addedActivities = addedActivities,
                    onDeleteCar = viewModel::deleteCar,
                    onDeleteActivity = viewModel::deleteActivity,
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
            title = stringResource(R.string.form_activity_header_title),
            subtitle = stringResource(R.string.form_activity_header_subtitle),
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
                value = if (cars.isEmpty()) stringResource(R.string.form_add_car_first) else selectedCar,
                onValueChange = {},
                readOnly = true,
                enabled = cars.isNotEmpty(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                label = { Text(stringResource(R.string.form_label_car)) },
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
                label = { Text(stringResource(R.string.form_label_activity_type)) },
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
            label = { Text(stringResource(R.string.form_label_title)) },
            placeholder = { Text(stringResource(R.string.form_placeholder_activity_title)) },
            singleLine = true,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.form_label_date)) },
                placeholder = { Text(stringResource(R.string.form_placeholder_date)) },
                singleLine = true,
            )

            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.form_label_mileage)) },
                placeholder = { Text(stringResource(R.string.form_placeholder_mileage)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        OutlinedTextField(
            value = workshop,
            onValueChange = { workshop = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.form_label_workshop)) },
            placeholder = { Text(stringResource(R.string.form_placeholder_workshop)) },
            singleLine = true,
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.form_label_notes)) },
            placeholder = { Text(stringResource(R.string.form_placeholder_notes)) },
            minLines = 4,
        )

        FormActions(
            saveText = stringResource(R.string.form_save_activity),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCarTab(
    makes: List<String>,
    modelsByMake: Map<String, List<String>>,
    isCatalogLoading: Boolean,
    catalogError: String?,
    onRetryCatalog: () -> Unit,
    onSave: (CarSummaryUi) -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    var nickname by rememberSaveable { mutableStateOf("") }
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var mileage by rememberSaveable { mutableStateOf("") }

    var makeExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        capturedImage = bitmap
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.form_camera_permission_denied),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            val displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            if (cursor.moveToFirst() && displayNameIndex >= 0) cursor.getString(displayNameIndex) else null
        }

        if (!name.isNullOrBlank()) {
            nickname = name
        }
    }

    val hasCatalog = makes.isNotEmpty()
    val selectedModels = remember(make, modelsByMake) {
        modelsByMake[make].orEmpty()
    }

    LaunchedEffect(makes) {
        if (make.isBlank() && makes.isNotEmpty()) {
            make = makes.first()
        }
    }

    LaunchedEffect(make, selectedModels) {
        if (selectedModels.isNotEmpty() && model !in selectedModels) {
            model = selectedModels.first()
        }
    }

    val canSave = nickname.isNotBlank() && make.isNotBlank() && model.isNotBlank() && year.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormHeader(
            title = stringResource(R.string.form_car_header_title),
            subtitle = stringResource(R.string.form_car_header_subtitle),
        )

        if (isCatalogLoading) {
            Text(
                text = stringResource(R.string.form_catalog_loading),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (catalogError != null) {
            Text(
                text = catalogError,
                color = MaterialTheme.colorScheme.error,
            )
            OutlinedButton(onClick = onRetryCatalog) {
                Text(stringResource(R.string.form_retry))
            }
        }

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.form_label_nickname)) },
            placeholder = { Text(stringResource(R.string.form_placeholder_nickname)) },
            singleLine = true,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AssistChip(
                onClick = { contactPickerLauncher.launch(null) },
                label = { Text(stringResource(R.string.form_import_from_contacts)) },
            )
            AssistChip(
                onClick = {
                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA,
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasCameraPermission) {
                        cameraLauncher.launch(null)
                        Toast.makeText(
                            context,
                            context.getString(R.string.form_camera_hint),
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                label = { Text(stringResource(R.string.form_take_photo)) },
            )
        }

        capturedImage?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.form_car_photo_preview),
                modifier = Modifier.size(96.dp),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ExposedDropdownMenuBox(
                expanded = makeExpanded,
                onExpandedChange = {
                    if (hasCatalog) {
                        makeExpanded = !makeExpanded
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                OutlinedTextField(
                    value = make,
                    onValueChange = {
                        if (!hasCatalog) {
                            make = it
                        }
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        .fillMaxWidth(),
                    readOnly = hasCatalog,
                    label = { Text(stringResource(R.string.form_label_make)) },
                    placeholder = { Text(stringResource(R.string.form_placeholder_make)) },
                    trailingIcon = {
                        if (hasCatalog) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = makeExpanded)
                        }
                    },
                    singleLine = true,
                )

                if (hasCatalog) {
                    ExposedDropdownMenu(
                        expanded = makeExpanded,
                        onDismissRequest = { makeExpanded = false },
                    ) {
                        makes.forEach { makeItem ->
                            DropdownMenuItem(
                                text = { Text(makeItem) },
                                onClick = {
                                    make = makeItem
                                    makeExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = modelExpanded,
                onExpandedChange = {
                    if (selectedModels.isNotEmpty()) {
                        modelExpanded = !modelExpanded
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                OutlinedTextField(
                    value = model,
                    onValueChange = {
                        if (selectedModels.isEmpty()) {
                            model = it
                        }
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                        .fillMaxWidth(),
                    readOnly = selectedModels.isNotEmpty(),
                    label = { Text(stringResource(R.string.form_label_model)) },
                    placeholder = { Text(stringResource(R.string.form_placeholder_model)) },
                    trailingIcon = {
                        if (selectedModels.isNotEmpty()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded)
                        }
                    },
                    singleLine = true,
                )

                if (selectedModels.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = modelExpanded,
                        onDismissRequest = { modelExpanded = false },
                    ) {
                        selectedModels.forEach { modelItem ->
                            DropdownMenuItem(
                                text = { Text(modelItem) },
                                onClick = {
                                    model = modelItem
                                    modelExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.form_label_year)) },
                placeholder = { Text(stringResource(R.string.form_placeholder_year)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.form_label_mileage)) },
                placeholder = { Text(stringResource(R.string.form_placeholder_mileage)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        OutlinedTextField(
            value = plate,
            onValueChange = { plate = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.form_label_plate)) },
            placeholder = { Text(stringResource(R.string.form_placeholder_plate)) },
            singleLine = true,
        )

        FormActions(
            saveText = stringResource(R.string.form_save_car),
            saveEnabled = canSave,
            onSave = {
                val photoPath = capturedImage?.let { bitmap -> saveCarPhoto(context, bitmap) }
                onSave(
                    CarSummaryUi(
                        id = UUID.randomUUID().toString(),
                        nickname = nickname,
                        make = make,
                        model = model,
                        year = year.toIntOrNull() ?: 0,
                        plate = plate,
                        mileage = mileage,
                        photoPath = photoPath,
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
    onDeleteCar: (CarSummaryUi) -> Unit,
    onDeleteActivity: (ActivityUi) -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormHeader(
            title = stringResource(R.string.form_saved_header_title),
            subtitle = stringResource(R.string.form_saved_header_subtitle),
        )

        Text(
            text = stringResource(R.string.form_saved_cars, addedCars.size),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (addedCars.isEmpty()) {
            Text(
                text = stringResource(R.string.form_saved_cars_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            addedCars.forEach { car ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(
                            R.string.form_saved_car_item,
                            car.nickname,
                            car.make,
                            car.model,
                            car.year,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    car.photoPath?.let { path ->
                        val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = stringResource(R.string.form_car_photo_preview),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp),
                            )
                        }
                    }
                    IconButton(onClick = { onDeleteCar(car) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.form_saved_delete_car),
                        )
                    }
                    IconButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    context.getString(
                                        R.string.share_car_template,
                                        car.nickname,
                                        car.year.toString(),
                                        car.make,
                                        car.model,
                                        car.plate,
                                        car.mileage,
                                    ),
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(sendIntent, context.getString(R.string.share_car_chooser_title)),
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.form_share_car),
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        Text(
            text = stringResource(R.string.form_saved_activities, addedActivities.size),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (addedActivities.isEmpty()) {
            Text(
                text = stringResource(R.string.form_saved_activities_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            addedActivities.forEach { activity ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(
                            R.string.form_saved_activity_item,
                            activity.date,
                            activity.title,
                            activity.carName,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { onDeleteActivity(activity) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.form_saved_delete_activity),
                        )
                    }
                    IconButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    context.getString(
                                        R.string.share_activity_template,
                                        activity.title,
                                        activity.carName,
                                        activity.date,
                                        activity.mileage,
                                        activity.description,
                                    ),
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(sendIntent, context.getString(R.string.share_activity_chooser_title)),
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.form_share_activity),
                        )
                    }
                }
            }
        }
    }
}

private fun saveCarPhoto(context: android.content.Context, bitmap: Bitmap): String? {
    return runCatching {
        val directory = File(context.filesDir, "car_photos")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val photoFile = File(directory, "car_${System.currentTimeMillis()}.jpg")
        FileOutputStream(photoFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
        photoFile.absolutePath
    }.getOrNull()
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
            Text(stringResource(R.string.form_cancel))
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
