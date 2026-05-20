package com.whatstheweather.app.presentation.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    var advancedExpanded by remember { mutableStateOf(false) }
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

            Spacer(Modifier.height(16.dp))

            // ─── Advanced Settings ────────────────────────────────────────
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { advancedExpanded = !advancedExpanded },
                cornerRadius = 16.dp, alpha = 0.1f
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Tune, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Advanced Settings", style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text("API provider & keys", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.55f))
                        }
                    }
                    Icon(
                        if (advancedExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        null, tint = Color.White.copy(0.6f)
                    )
                }
            }

            AnimatedVisibility(
                visible = advancedExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 4.dp),
                    cornerRadius = 16.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        // API provider selection
                        Text("Weather Data Source", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(0.65f))

                        WeatherApiProvider.values().forEach { provider ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.updateSettings(settings.copy(apiProvider = provider))
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.apiProvider == provider,
                                    onClick = { viewModel.updateSettings(settings.copy(apiProvider = provider)) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color.White, unselectedColor = Color.White.copy(0.4f))
                                )
                                Column {
                                    Text(provider.displayName(), color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    Text(provider.subtitle(), color = Color.White.copy(0.5f), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Divider(color = Color.White.copy(0.1f))

                        // API key inputs
                        if (settings.apiProvider == WeatherApiProvider.OPEN_WEATHER_MAP) {
                            ApiKeyField(
                                label = "OpenWeatherMap API Key",
                                value = settings.openWeatherMapApiKey,
                                onValueChange = { viewModel.updateSettings(settings.copy(openWeatherMapApiKey = it)) }
                            )
                        }
                        if (settings.apiProvider == WeatherApiProvider.WEATHER_API) {
                            ApiKeyField(
                                label = "WeatherAPI.com API Key",
                                value = settings.weatherApiKey,
                                onValueChange = { viewModel.updateSettings(settings.copy(weatherApiKey = it)) }
                            )
                        }
                        if (settings.apiProvider == WeatherApiProvider.TOMORROW_IO) {
                            ApiKeyField(
                                label = "Tomorrow.io API Key",
                                value = settings.tomorrowApiKey,
                                onValueChange = { viewModel.updateSettings(settings.copy(tomorrowApiKey = it)) }
                            )
                        }

                    }
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

@Composable
private fun ApiKeyField(label: String, value: String, onValueChange: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(0.6f)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null, tint = Color.White.copy(0.5f))
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White.copy(0.4f), unfocusedBorderColor = Color.White.copy(0.2f),
            cursorColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(12.dp)
    )
}

private fun WeatherApiProvider.displayName() = when (this) {
    WeatherApiProvider.OPEN_METEO -> "Open-Meteo"
    WeatherApiProvider.OPEN_WEATHER_MAP -> "OpenWeatherMap"
    WeatherApiProvider.WEATHER_API -> "WeatherAPI.com"
    WeatherApiProvider.TOMORROW_IO -> "Tomorrow.io"
}

private fun WeatherApiProvider.subtitle() = when (this) {
    WeatherApiProvider.OPEN_METEO -> "Free · No API key required · Default"
    WeatherApiProvider.OPEN_WEATHER_MAP -> "Free tier · Requires API key"
    WeatherApiProvider.WEATHER_API -> "Free tier · Requires API key"
    WeatherApiProvider.TOMORROW_IO -> "Free tier · Requires API key"
}
