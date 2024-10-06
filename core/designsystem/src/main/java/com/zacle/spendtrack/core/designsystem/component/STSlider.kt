package com.zacle.spendtrack.core.designsystem.component

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme

@Composable
fun STSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SliderWithPercentageIndicator(
            value = value / 100f,
            onValueChange = { onValueChange(it * 100) },
            percentage = value.toInt(),
            thumbSize = 36.dp
        )
    }
}

@Composable
fun SliderWithPercentageIndicator(
    value: Float,
    onValueChange: (Float) -> Unit,
    percentage: Int,
    thumbSize: Dp = 32.dp
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(thumbSize)
    ) {
        val sliderWidth = maxWidth

        // Background slider bar
        SliderBackground(thumbSize = thumbSize)

        // Foreground progress bar (filled part)
        SliderProgressBar(
            value = value,
            sliderWidth = sliderWidth,
            thumbSize = thumbSize
        )

        // Thumb with percentage inside it
        ThumbWithPercentage(
            percentage = percentage,
            value = value,
            sliderWidth = sliderWidth,
            thumbSize = thumbSize,
            cornerRadius = 12.dp // Add corner radius for rounded effect
        )

        // Slider control for touch input
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,   // We will handle the thumb ourselves
                activeTrackColor = Color.Transparent, // Remove the default track
                inactiveTrackColor = Color.Transparent // Remove the default track
            )
        )
    }
}

@Composable
fun SliderBackground(thumbSize: Dp) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRoundRect(
            color = Color.LightGray,
            cornerRadius = CornerRadius(thumbSize.toPx() / 2),
            size = Size(width = size.width, height = thumbSize.toPx() / 2)
        )
    }
}

@Composable
fun SliderProgressBar(
    value: Float,
    sliderWidth: Dp,
    thumbSize: Dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val progressWidth = sliderWidth.toPx() * value
        drawRoundRect(
            color = backgroundColor.copy(alpha = 0.8f),
            cornerRadius = CornerRadius(thumbSize.toPx() / 2),
            size = Size(width = progressWidth, height = thumbSize.toPx() / 2)
        )
    }
}

@Composable
fun ThumbWithPercentage(
    percentage: Int,
    value: Float,
    sliderWidth: Dp,
    thumbSize: Dp,
    cornerRadius: Dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Calculate the thumb position based on the current slider value
        val thumbPositionX = sliderWidth.toPx() * value - thumbSize.toPx() / 2
        val thumbPositionY = size.height - thumbSize.toPx() - 24 // Centering vertically

        // Draw the thumb circle
        translate(left = thumbPositionX, top = thumbPositionY) {
            drawRoundRect(
                color = backgroundColor, // Inner rectangle color (white)
                size = Size(thumbSize.toPx() - 2.dp.toPx(), thumbSize.toPx() - 2.dp.toPx()), // Padding for inner rectangle
                topLeft = Offset(1.dp.toPx(), 1.dp.toPx()), // Padding from the outer thumb
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )

            // Draw percentage text inside the thumb
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = Paint().apply {
                    color = contentColor.toArgb()
                    textSize = textStyle.fontSize.value.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true // Enable anti-aliasing for smoother edges
                    style = Paint.Style.FILL // Set paint style to fill
                }
                drawText(
                    "$percentage%",
                    thumbSize.toPx() / 2, // Horizontal center of the circle
                    thumbSize.toPx() / 2 - (textPaint.ascent() + textPaint.descent()) / 2, // Vertical center
                    textPaint
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun STSliderPreview() {
    var sliderValue by remember { mutableFloatStateOf(80f) }
    SpendTrackTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            STSlider(
                value = sliderValue,
                onValueChange = { sliderValue = it }
            )
        }
    }
}