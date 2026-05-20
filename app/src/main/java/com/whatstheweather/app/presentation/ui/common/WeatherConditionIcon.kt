package com.whatstheweather.app.presentation.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.whatstheweather.app.domain.model.WeatherCondition

@Composable
fun WeatherConditionIcon(
    condition: WeatherCondition,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = Color.White
) {
    val (icon, iconTint) = getConditionIconAndTint(condition, isDay)
    Icon(
        imageVector = icon,
        contentDescription = condition.name,
        tint = iconTint ?: tint,
        modifier = modifier.size(size)
    )
}

private fun getConditionIconAndTint(
    condition: WeatherCondition,
    isDay: Boolean
): Pair<ImageVector, Color?> = when (condition) {
    WeatherCondition.CLEAR_SKY -> if (isDay)
        Icons.Outlined.WbSunny to Color(0xFFFFF176)
    else
        Icons.Outlined.NightsStay to Color(0xFFE8EAF6)

    WeatherCondition.PARTLY_CLOUDY -> if (isDay)
        Icons.Outlined.WbCloudy to Color(0xFFB0BEC5)
    else
        Icons.Outlined.NightsStay to Color(0xFFB0BEC5)

    WeatherCondition.OVERCAST ->
        Icons.Filled.Cloud to Color(0xFF90A4AE)

    WeatherCondition.FOG ->
        Icons.Outlined.FilterDrama to Color(0xFFCFD8DC)

    WeatherCondition.DRIZZLE ->
        Icons.Outlined.Grain to Color(0xFF81D4FA)

    WeatherCondition.RAIN, WeatherCondition.RAIN_SHOWERS ->
        Icons.Outlined.Thunderstorm to Color(0xFF64B5F6)

    WeatherCondition.SNOW, WeatherCondition.SNOW_SHOWERS ->
        Icons.Outlined.AcUnit to Color(0xFFE3F2FD)

    WeatherCondition.THUNDERSTORM, WeatherCondition.THUNDERSTORM_WITH_HAIL ->
        Icons.Filled.Thunderstorm to Color(0xFFFFD54F)

    WeatherCondition.UNKNOWN ->
        Icons.Outlined.WbCloudy to null
}
