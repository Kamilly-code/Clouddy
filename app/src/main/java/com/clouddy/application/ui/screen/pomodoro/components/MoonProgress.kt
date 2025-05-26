package com.clouddy.application.ui.screen.pomodoro.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.clouddy.application.R

@Composable
fun MoonProgress(timeRemaining: Int, totalTime: Int) {
    val progress = 1f - (timeRemaining.toFloat() / totalTime.toFloat())
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.luna_llena)


    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {

        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.width(200.dp).clip(CircleShape)
        )


        Canvas(
            modifier = Modifier
                .size(157.dp)
                .clip(CircleShape)
        ) {
            val rectWidth = size.width * progress
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 1f), Color.Black.copy(alpha = 0.6f)),
                    center = Offset(size.width - rectWidth, size.height / 2),
                    radius = size.width / 2
                ),
                topLeft = Offset(size.width - rectWidth, 0f),
                size = Size(rectWidth, size.height),
                blendMode = BlendMode.SrcOver
            )
        }
    }
}
