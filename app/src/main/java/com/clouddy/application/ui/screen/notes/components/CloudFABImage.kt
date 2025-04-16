package com.clouddy.application.ui.screen.notes.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clouddy.application.R

@Composable
fun CloudFABImage(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(90.dp)
            .clickable { onClick() },
    ) {
        Image(
            painter = painterResource(id = R.drawable.fbnube),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .size(100.dp)
                .offset(y = 5.dp, x = 5.dp)
                .blur(radius = 12.dp)
                .alpha(0.6f)
        )

        Image(
            painter = painterResource(id = R.drawable.fbnube),
            contentDescription = "Botón de acción flotante de nube",
            modifier = Modifier
                .size(90.dp)
        )
    }
}


