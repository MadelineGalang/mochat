package com.dm.mochat.watch.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Red400 = Color(0xFFCF6679)

val NavyBlue = Color(0xFF2F4156)
val Cinder = Color(0xFF1E2A32)
val BlackPearl = Color(0xFF0E181E) // dark font
val LightSkyBlue = Color(0xFFA2C9FF)
val LightPowderBlue = Color(0xFFD7E2FF)
val LightCyan = Color(0xFFE0F1FF) // light font

internal val wearColorPalette: Colors = Colors(
    primary = LightSkyBlue,
    primaryVariant = LightPowderBlue,
    secondary = NavyBlue,
    secondaryVariant = Cinder,
    error = Red400,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onError = Color.Black
)