package com.katiacammisa.mycar.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.katiacammisa.mycar.home.data.ActivityType
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi

@Composable
fun FamilyCarsHomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onCarClick: (CarSummaryUi) -> Unit = {},
    onActivityClick: (ActivityUi) -> Unit = {},
) {
    val cars by viewModel.cars.collectAsStateWithLifecycle()
    val latestActivities by viewModel.activities.collectAsStateWithLifecycle()

    FamilyCarsHomeScreen(
        modifier = modifier,
        cars = cars,
        latestActivities = latestActivities,
        onCarClick = onCarClick,
        onActivityClick = onActivityClick,
        onDeleteActivity = { activity -> viewModel.deleteActivity(activity.id) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyCarsHomeScreen(
    modifier: Modifier = Modifier,
    cars: List<CarSummaryUi> = previewCars,
    latestActivities: List<ActivityUi> = previewActivities,
    onCarClick: (CarSummaryUi) -> Unit = {},
    onActivityClick: (ActivityUi) -> Unit = {},
    onDeleteActivity: (ActivityUi) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            WelcomeHeader(
                totalCars = cars.size,
                totalActivities = latestActivities.size,
            )
        }

        item {
            SectionTitle(title = "My Cars")
        }

        items(cars, key = { it.id }) { car ->
            CarSummaryCard(
                car = car,
                onClick = { onCarClick(car) },
            )
        }

        item {
            SectionTitle(title = "Latest Activity")
        }

        items(latestActivities, key = { it.id }) { activity ->
            ActivityCard(
                activity = activity,
                onClick = { onActivityClick(activity) },
                onDelete = { onDeleteActivity(activity) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun WelcomeHeader(
    totalCars: Int,
    totalActivities: Int,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = "Keep your family vehicles organized",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add each car with details like make, model, year, mileage, and plate. Then register maintenance like oil changes, tire replacements, inspections, and more.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatChip(label = "Cars", value = totalCars.toString())
                StatChip(label = "Recent logs", value = totalActivities.toString())
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun CarSummaryCard(
    car: CarSummaryUi,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = car.nickname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${car.year} ${car.make} ${car.model}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Plate: ${car.plate} • Mileage: ${car.mileage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun ActivityCard(
    activity: ActivityUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ActivityIcon(type = activity.type)

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = activity.carName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Text(
                    text = activity.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete activity",
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Mileage at service: ${activity.mileage}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActivityIcon(
    type: ActivityType,
) {
    val icon = when (type) {
        ActivityType.OIL_CHANGE -> Icons.Outlined.Build
        ActivityType.TIRE_CHANGE -> Icons.Outlined.Build
        ActivityType.BRAKE_SERVICE -> Icons.Outlined.Build
        ActivityType.BATTERY_CHECK -> Icons.Outlined.Build
        ActivityType.INSPECTION -> Icons.Outlined.Build
        ActivityType.FUEL -> Icons.Outlined.Build
        ActivityType.GENERAL_SERVICE -> Icons.Outlined.Build
        ActivityType.OTHER -> Icons.Outlined.Build
    }

    val background = when (type) {
        ActivityType.OIL_CHANGE -> Color(0xFFE8F0FE)
        ActivityType.TIRE_CHANGE -> Color(0xFFFFF4E5)
        ActivityType.BRAKE_SERVICE -> Color(0xFFE9F7EF)
        ActivityType.BATTERY_CHECK -> Color(0xFFF3E8FF)
        ActivityType.INSPECTION -> Color(0xFFE9F7EF)
        ActivityType.FUEL -> Color(0xFFF3E8FF)
        ActivityType.GENERAL_SERVICE -> Color(0xFFFFEBEE)
        ActivityType.OTHER -> Color(0xFFF5F5F5)
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private val previewCars = listOf(
    CarSummaryUi(
        id = "1",
        nickname = "Kati",
        make = "Toyota",
        model = "RAV4",
        year = 2021,
        plate = "AB123CD",
        mileage = "42,180 km",
    ),
    CarSummaryUi(
        id = "2",
        nickname = "Joaco",
        make = "Ford",
        model = "Fiesta",
        year = 2018,
        plate = "EF456GH",
        mileage = "67,520 km",
    ),
    CarSummaryUi(
        id = "3",
        nickname = "Mario",
        make = "Honda",
        model = "Odyssey",
        year = 2020,
        plate = "IJ789KL",
        mileage = "51,030 km",
    ),
    CarSummaryUi(
        id = "4",
        nickname = "Patri",
        make = "Audi",
        model = "Q7",
        year = 2022,
        plate = "HF123PQ",
        mileage = "51,030 km",
    ),
)

private val previewActivities = listOf(
    ActivityUi(
        id = "99",
        carName = "Kati",
        title = "Oil Change",
        description = "Changed engine oil and replaced oil filter at the local workshop.",
        date = "18/3/2026",
        mileage = "42,180 km",
        type = ActivityType.OIL_CHANGE,
    ),
    ActivityUi(
        id = "100",
        carName = "Joaco",
        title = "Tire Replacement",
        description = "Installed two new front tires and checked wheel alignment.",
        date = "23/2/2026",
        mileage = "67,420 km",
        type = ActivityType.TIRE_CHANGE,
    ),
    ActivityUi(
        id = "101",
        carName = "Mario",
        title = "Annual Inspection",
        description = "Completed general inspection including brakes, lights, and fluids.",
        date = "12/1/2026",
        mileage = "50,870 km",
        type = ActivityType.INSPECTION,
    ),
    ActivityUi(
        id = "102",
        carName = "Kati",
        title = "General Service",
        description = "Replaced air filter, topped up coolant, and checked battery health.",
        date = "28/12/2025",
        mileage = "40,950 km",
        type = ActivityType.GENERAL_SERVICE,
    ),
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FamilyCarsHomeScreenPreview() {
    MaterialTheme {
        FamilyCarsHomeScreen()
    }
}
