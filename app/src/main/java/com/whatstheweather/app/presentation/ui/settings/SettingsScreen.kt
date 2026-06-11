package com.whatstheweather.app.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whatstheweather.app.domain.model.*
import com.whatstheweather.app.presentation.theme.GradNightBottom
import com.whatstheweather.app.presentation.theme.GradNightTop
import com.whatstheweather.app.presentation.ui.common.GlassCard

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GradNightTop, GradNightBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = Color.White)
                }
                Text("Settings", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }

            Spacer(Modifier.height(8.dp))

            // ─── General Settings ─────────────────────────────────────────
            SettingsSectionHeader("General")

            GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), cornerRadius = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                    // Temperature unit
                    SegmentedRow(
                        label = "Temperature",
                        icon = Icons.Outlined.Thermostat,
                        options = listOf("Celsius", "Fahrenheit"),
                        selected = settings.temperatureUnit.ordinal,
                        onSelect = { viewModel.updateSettings(settings.copy(temperatureUnit = TemperatureUnit.values()[it])) }
                    )
                    Divider(color = Color.White.copy(0.1f), thickness = 0.5.dp)

                    // Wind unit
                    SegmentedRow(
                        label = "Wind Speed",
                        icon = Icons.Outlined.Air,
                        options = listOf("km/h", "mph", "m/s"),
                        selected = settings.windSpeedUnit.ordinal,
                        onSelect = { viewModel.updateSettings(settings.copy(windSpeedUnit = WindSpeedUnit.values()[it])) }
                    )
                    Divider(color = Color.White.copy(0.1f), thickness = 0.5.dp)

                    // Time format
                    SegmentedRow(
                        label = "Time Format",
                        icon = Icons.Outlined.Schedule,
                        options = listOf("12h", "24h"),
                        selected = settings.timeFormat.ordinal,
                        onSelect = { viewModel.updateSettings(settings.copy(timeFormat = TimeFormat.values()[it])) }
                    )
                    Divider(color = Color.White.copy(0.1f), thickness = 0.5.dp)

                    // Theme
                    SegmentedRow(
                        label = "Theme",
                        icon = Icons.Outlined.DarkMode,
                        options = listOf("System", "Light", "Dark"),
                        selected = settings.theme.ordinal,
                        onSelect = { viewModel.updateSettings(settings.copy(theme = AppTheme.values()[it])) }
                    )
                    Divider(color = Color.White.copy(0.1f), thickness = 0.5.dp)

                    // Animated background
                    SwitchRow(
                        label = "Animated Background",
                        icon = Icons.Outlined.Animation,
                        subtitle = "Rain, snow & cloud effects",
                        checked = settings.animatedBackground,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(animatedBackground = it)) }
                    )
                    Divider(color = Color.White.copy(0.1f), thickness = 0.5.dp)

                    // Notifications
                    SwitchRow(
                        label = "Weather Alerts",
                        icon = Icons.Outlined.Notifications,
                        subtitle = "Severe weather notifications",
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(notificationsEnabled = it)) }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Color.White.copy(0.5f),
        modifier = Modifier.padding(start = 28.dp, bottom = 8.dp)
    )
}

@Composable
private fun SegmentedRow(
    label: String,
    icon: ImageVector,
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(icon, null, tint = Color.White.copy(0.65f), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { onSelect(index) },
                    selected = selected == index,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color.White.copy(0.25f),
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = Color.White.copy(0.5f),
                        activeBorderColor = Color.White.copy(0.3f),
                        inactiveBorderColor = Color.White.copy(0.15f)
                    )
                ) {
                    Text(option, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    icon: ImageVector,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White.copy(0.65f), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text(subtitle, color = Color.White.copy(0.5f), style = MaterialTheme.typography.bodySmall)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.White.copy(0.35f),
                uncheckedThumbColor = Color.White.copy(0.5f),
                uncheckedTrackColor = Color.White.copy(0.1f)
            )
        )
    }
}


