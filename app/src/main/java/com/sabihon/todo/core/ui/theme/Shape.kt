package com.sabihon.todo.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape tokens – cards 24.dp, chips/pills 100.dp (full), buttons 16.dp
 */
val SabihonShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp), // buttons
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp), // cards
    extraLarge = RoundedCornerShape(100.dp) // pills / chips
)
