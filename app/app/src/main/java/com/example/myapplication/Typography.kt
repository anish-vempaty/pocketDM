package com.example.myapplication

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Replace with a dot matrix font
val dotMatrixFontFamily = FontFamily.Monospace

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = dotMatrixFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
