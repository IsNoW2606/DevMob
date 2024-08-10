package com.example.simongame.view.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.example.simongame.view.effect.dropShadow

@Composable
fun ElevatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = Color.Black,
    shape: Shape,
    offset: Dp,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val interSource = remember { MutableInteractionSource() }
    val isPressed by interSource.collectIsPressedAsState()

    // Animate shadow offset to create a hide shadow animation on press
    val shadowOffset by animateDpAsState(
        targetValue = if (isPressed) 0.dp else offset, label = "shadowOffsetY"
    )

    val color: Color = if (enabled) buttonColor else Color(ColorUtils.blendARGB(buttonColor.toArgb(), Color.DarkGray.copy(alpha = 0.8f).toArgb(), 0.5f))
    Box(
        modifier = modifier
            .dropShadow(shape = shape, offsetY = shadowOffset, widthRatio = 0.95f)
            .clip(shape = shape)
            .background(color)
            .clickable(
                interactionSource = interSource,
                indication = null,
                onClick = onClick,
                enabled = enabled
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}