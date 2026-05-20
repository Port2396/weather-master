package com.whatstheweather.app.presentation.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.whatstheweather.app.presentation.theme.GlassBorder
import com.whatstheweather.app.presentation.theme.GlassSurface

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    alpha: Float = 0.15f,
    borderAlpha: Float = 0.4f,
    content: @Composable BoxScope.() -> Unit
) {
    val glassBackground = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = alpha),
            Color.White.copy(alpha = alpha * 0.5f)
        )
    )
    val glassBorder = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = borderAlpha),
            Color.White.copy(alpha = borderAlpha * 0.3f),
            Color.White.copy(alpha = borderAlpha * 0.1f)
        )
    )
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind { drawGlassBackground(glassBackground) }
            .border(width = 1.dp, brush = glassBorder, shape = shape)
            .padding(16.dp),
        content = content
    )
}

private fun DrawScope.drawGlassBackground(brush: Brush) {
    drawRect(brush = brush)
}
