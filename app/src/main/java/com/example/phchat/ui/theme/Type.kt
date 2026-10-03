package com.example.phchat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Phchat semantic type scale. Avoid one-off text sizes in screens. */
val Typography = Typography(
    displaySmall = TextStyle(FontFamily.Default, FontWeight.Black, 32.sp, 38.sp, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(FontFamily.Default, FontWeight.Bold, 28.sp, 34.sp, letterSpacing = (-0.2).sp),
    headlineMedium = TextStyle(FontFamily.Default, FontWeight.Bold, 24.sp, 30.sp),
    headlineSmall = TextStyle(FontFamily.Default, FontWeight.SemiBold, 20.sp, 26.sp),
    titleLarge = TextStyle(FontFamily.Default, FontWeight.SemiBold, 18.sp, 24.sp),
    titleMedium = TextStyle(FontFamily.Default, FontWeight.SemiBold, 16.sp, 22.sp),
    bodyLarge = TextStyle(FontFamily.Default, FontWeight.Normal, 16.sp, 24.sp),
    bodyMedium = TextStyle(FontFamily.Default, FontWeight.Normal, 14.sp, 20.sp),
    labelLarge = TextStyle(FontFamily.Default, FontWeight.SemiBold, 13.sp, 18.sp),
    labelMedium = TextStyle(FontFamily.Default, FontWeight.Medium, 12.sp, 16.sp),
    labelSmall = TextStyle(FontFamily.Default, FontWeight.Medium, 11.sp, 14.sp)
)
