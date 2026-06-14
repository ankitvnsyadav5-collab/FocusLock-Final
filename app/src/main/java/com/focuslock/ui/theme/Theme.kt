package com.focuslock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val FocusLockColorScheme = darkColorScheme(
    primary           = Purple500,
    onPrimary         = TextOnPurple,
    primaryContainer  = PurpleDim,
    onPrimaryContainer= PurpleLight,
    secondary         = PurpleLight,
    onSecondary       = BackgroundDark,
    secondaryContainer= CardDark,
    onSecondaryContainer = TextSecondary,
    tertiary          = GreenAccent,
    background        = BackgroundDark,
    onBackground      = TextPrimary,
    surface           = SurfaceDark,
    onSurface         = TextPrimary,
    surfaceVariant    = CardDark,
    onSurfaceVariant  = TextSecondary,
    outline           = DividerColor,
    outlineVariant    = CardDark2,
    error             = RedAccent,
    onError           = TextOnPurple,
    scrim             = Color(0x99000000)
)

@Composable
fun FocusLockTheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = BackgroundDark,
            darkIcons = false
        )
    }
    MaterialTheme(
        colorScheme = FocusLockColorScheme,
        typography  = Typography,
        content     = content
    )
}
