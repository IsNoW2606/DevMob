package com.example.simongame.view.effect

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dropShadow(
    shape: Shape,
    color: Color = Color.Black.copy(0.1f),
    offsetY: Dp = 1.5.dp,
    offsetX: Dp = 0.dp,
    widthRatio: Float = 1f,
    heightRatio: Float = 1f
) = this.drawBehind {

    val shadowSize = Size(size.width.times(widthRatio), size.height.times(heightRatio))

    val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

    // Create a Paint object
    val paint = Paint()
    // Apply specified color
    paint.color = color

    drawIntoCanvas { canvas ->
        // Save the canvas state
        canvas.save()
        // Translate to specified offsets
        canvas.translate(offsetX.toPx() + size.width.times((1f - widthRatio).div(2)), offsetY.toPx() + size.height.times((1f - heightRatio).div(2)))
        // Draw the shadow
        canvas.drawOutline(shadowOutline, paint)
        // Restore the canvas state
        canvas.restore()
    }

}