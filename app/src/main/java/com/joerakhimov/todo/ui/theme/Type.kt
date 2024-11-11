package com.joerakhimov.todo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    titleLarge = TextStyle( // Title Large
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.Medium // 500
    ),
    titleMedium = TextStyle( // Title
        fontSize = 20.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Medium // 500
    ),
    labelLarge = TextStyle( // Button
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium // 500
    ),
    bodyMedium = TextStyle( // Body
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal // 400
    ),
    labelSmall = TextStyle( // Subhead
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal // 400
    )
)