package com.clouddy.application.ui.screen.notes.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clouddy.application.R
import kotlin.comparisons.then

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CloudFABImage(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(90.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.fbnube),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationX = 10f
                    translationY = 8f
                    alpha = 0.3f
                    shadowElevation = 0f
                    clip = false
                }
                .blur(2.dp)
        )


        Image(
            painter = painterResource(id = R.drawable.fbnube),
            contentDescription = "Botón de acción flotante de nube",
            modifier = Modifier.size(90.dp)
        )
    }
}

