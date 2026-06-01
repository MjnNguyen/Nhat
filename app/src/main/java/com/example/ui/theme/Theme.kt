package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryTeal,
    secondary = SecondaryTeal,
    background = SlateDarkBackground,
    surface = SlateDarkSurface,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SecondaryTeal,
    secondary = PrimaryTeal,
    background = Color.White,
    surface = Color(0xFFF8F9FA)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark mode to eliminate eye strain
  dynamicColor: Boolean = false, // Use our stylized palette for cohesive brand presentation
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
