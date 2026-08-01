package com.eaxor.easy2share.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaxor.easy2share.ui.theme.IndustrialInk

@Composable
fun BrutalistActionButton(
    icon: ImageVector,
    label: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 55, easing = LinearEasing),
        label = "homeActionPressOffset",
    )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(5.dp, 10.dp, 0.dp, 10.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(end = 0.dp, bottom = 5.dp)
                    .offset {
                        IntOffset(
                            x = pressOffset.toPx().toInt(),
                            y = pressOffset.toPx().toInt()
                        )
                    }
                    .background(containerColor)
                    .border(3.dp, IndustrialInk)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                    .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = label.uppercase(),
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 16.sp,
                )
                Box(
                    modifier =
                        Modifier
                            .width(38.dp)
                            .height(3.dp)
                            .background(IndustrialInk),
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IndustrialInk,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}
