package com.example.phchat.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

object PhchatMotion {
    const val quick = 120
    const val standard = 220
    const val emphasis = 320

    val standardTween = tween<Float>(durationMillis = standard, easing = FastOutSlowInEasing)
}
