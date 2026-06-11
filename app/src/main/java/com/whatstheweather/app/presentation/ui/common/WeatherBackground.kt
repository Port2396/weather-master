package com.whatstheweather.app.presentation.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.whatstheweather.app.domain.model.WeatherCondition
import com.whatstheweather.app.presentation.theme.*
import kotlin.math.*
import kotlin.random.Random

@Composable
fun WeatherBackground(
    condition: WeatherCondition,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    animationsEnabled: Boolean = true
) {
    val (topColor, bottomColor) = getGradientColors(condition, isDay)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(topColor, bottomColor)))
    ) {
        if (animationsEnabled) {
            when {
                condition == WeatherCondition.RAIN ||
                condition == WeatherCondition.DRIZZLE ||
                condition == WeatherCondition.RAIN_SHOWERS -> RainAnimation()

                condition == WeatherCondition.SNOW ||
                condition == WeatherCondition.SNOW_SHOWERS -> SnowAnimation()

                condition == WeatherCondition.THUNDERSTORM ||
                condition == WeatherCondition.THUNDERSTORM_WITH_HAIL -> ThunderstormAnimation()

                condition == WeatherCondition.CLEAR_SKY && isDay -> SunAnimation()

                condition == WeatherCondition.PARTLY_CLOUDY -> CloudDriftAnimation(count = 3)

                condition == WeatherCondition.OVERCAST -> CloudDriftAnimation(count = 6, alpha = 0.12f)

                condition == WeatherCondition.FOG -> FogAnimation()

                !isDay -> StarfieldAnimation()
            }

            // Fade the animations toward the bottom so cards/text below stay legible.
            // Keeps the effects vivid up top, blending into the plain gradient lower down.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.35f to Color.Transparent,
                            1.0f to bottomColor.copy(alpha = 0.85f)
                        )
                    )
            )
        }
    }
}

private fun getGradientColors(condition: WeatherCondition, isDay: Boolean): Pair<Color, Color> =
    when {
        !isDay && (condition == WeatherCondition.CLEAR_SKY || condition == WeatherCondition.PARTLY_CLOUDY) ->
            GradNightTop to GradNightBottom
        !isDay -> GradNightCloudTop to GradNightCloudBottom
        condition == WeatherCondition.CLEAR_SKY || condition == WeatherCondition.PARTLY_CLOUDY ->
            GradSunnyTop to GradSunnyBottom
        condition == WeatherCondition.OVERCAST || condition == WeatherCondition.FOG ->
            GradCloudyTop to GradCloudyBottom
        condition == WeatherCondition.RAIN || condition == WeatherCondition.DRIZZLE ||
        condition == WeatherCondition.RAIN_SHOWERS -> GradRainTop to GradRainBottom
        condition == WeatherCondition.SNOW || condition == WeatherCondition.SNOW_SHOWERS ->
            GradSnowTop to GradSnowBottom
        condition == WeatherCondition.THUNDERSTORM || condition == WeatherCondition.THUNDERSTORM_WITH_HAIL ->
            GradStormTop to GradStormBottom
        else -> GradSunnyTop to GradSunnyBottom
    }

// ─── Rain ─────────────────────────────────────────────────────────────────────

data class RainDrop(val x: Float, val startY: Float, val speed: Float, val length: Float, val alpha: Float)

@Composable
fun RainAnimation() {
    val drops = remember {
        List(80) {
            RainDrop(
                x = Random.nextFloat(),
                startY = Random.nextFloat(),
                speed = Random.nextFloat() * 1500f + 800f,
                length = Random.nextFloat() * 20f + 10f,
                alpha = Random.nextFloat() * 0.3f + 0.1f
            )
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "rainTime"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drops.forEach { drop ->
            val y = ((drop.startY + time * (1000f / drop.speed)) % 1f) * size.height
            drawLine(
                color = Color.White.copy(alpha = drop.alpha),
                start = Offset(drop.x * size.width, y),
                end = Offset(drop.x * size.width - 2f, y + drop.length),
                strokeWidth = 1.5f
            )
        }
    }
}

// ─── Snow ─────────────────────────────────────────────────────────────────────

data class Snowflake(val x: Float, val startY: Float, val size: Float, val speed: Float, val alpha: Float, val drift: Float)

@Composable
fun SnowAnimation() {
    val flakes = remember {
        List(60) {
            Snowflake(
                x = Random.nextFloat(),
                startY = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 3000f + 2000f,
                alpha = Random.nextFloat() * 0.5f + 0.2f,
                drift = Random.nextFloat() * 0.02f - 0.01f
            )
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "snowTime"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        flakes.forEach { flake ->
            val progress = (flake.startY + time * (3000f / flake.speed)) % 1f
            val x = (flake.x + flake.drift * progress * 20f).coerceIn(0f, 1f) * size.width
            val y = progress * size.height
            drawCircle(
                color = Color.White.copy(alpha = flake.alpha),
                radius = flake.size,
                center = Offset(x, y)
            )
        }
    }
}

// ─── Sun Glow ─────────────────────────────────────────────────────────────────

@Composable
fun SunAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sunPulse"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width * 0.75f, size.height * 0.15f)
        val maxRadius = size.minDimension * 0.25f * pulse
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF176).copy(alpha = 0.25f * pulse),
                    Color.Transparent
                ),
                center = center,
                radius = maxRadius * 2f
            ),
            radius = maxRadius * 2f,
            center = center
        )
        drawCircle(
            color = Color(0xFFFFF176).copy(alpha = 0.15f),
            radius = maxRadius,
            center = center
        )
    }
}

// ─── Cloud Drift ──────────────────────────────────────────────────────────────

@Composable
fun CloudDriftAnimation(count: Int = 3, alpha: Float = 0.08f) {
    val infiniteTransition = rememberInfiniteTransition(label = "clouds")
    val offsets = (0 until count).map { i ->
        infiniteTransition.animateFloat(
            initialValue = -0.3f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                tween((20000 + i * 4000), easing = LinearEasing),
                RepeatMode.Restart,
                initialStartOffset = StartOffset(i * 3000)
            ),
            label = "cloud$i"
        )
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        offsets.forEachIndexed { i, offset ->
            val x = offset.value * size.width
            val y = size.height * (0.1f + i * 0.08f)
            val w = size.width * (0.4f + i * 0.1f)
            val h = w * 0.35f
            drawCloudShape(x, y, w, h, alpha)
        }
    }
}

private fun DrawScope.drawCloudShape(x: Float, y: Float, w: Float, h: Float, alpha: Float) {
    val color = Color.White.copy(alpha = alpha)
    drawOval(color = color, topLeft = Offset(x, y), size = androidx.compose.ui.geometry.Size(w, h))
    drawOval(color = color, topLeft = Offset(x + w * 0.2f, y - h * 0.3f), size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.7f))
    drawOval(color = color, topLeft = Offset(x + w * 0.5f, y - h * 0.2f), size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.6f))
}

// ─── Fog ──────────────────────────────────────────────────────────────────────

@Composable
fun FogAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "fog")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "fogPhase"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        for (i in 0..4) {
            val y = size.height * (0.3f + i * 0.12f)
            val alpha = (0.05f + sin((phase + i * 0.2f) * PI).toFloat() * 0.04f).coerceAtLeast(0f)
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = alpha), Color.Transparent)
                ),
                topLeft = Offset(0f, y),
                size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.08f)
            )
        }
    }
}

// ─── Thunderstorm ─────────────────────────────────────────────────────────────

@Composable
fun ThunderstormAnimation() {
    RainAnimation()
    val infiniteTransition = rememberInfiniteTransition(label = "storm")
    val lightning by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 4000
                0f at 0
                0f at 3200
                1f at 3300
                0f at 3400
                0.7f at 3500
                0f at 3600
            },
            RepeatMode.Restart
        ),
        label = "lightning"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (lightning > 0f) {
            drawRect(Color.White.copy(alpha = lightning * 0.15f))
        }
    }
}

// ─── Stars ────────────────────────────────────────────────────────────────────

@Composable
fun StarfieldAnimation() {
    val stars = remember {
        List(80) { Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 2f + 1f) }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "twinkle"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { (x, y, phase) ->
            val alpha = (0.3f + sin(twinkle + phase).toFloat() * 0.3f).coerceAtLeast(0.05f)
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 1.5f,
                center = Offset(x * size.width, y * size.height * 0.6f)
            )
        }
    }
}
