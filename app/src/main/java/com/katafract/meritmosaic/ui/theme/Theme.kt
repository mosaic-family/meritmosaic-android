package com.katafract.meritmosaic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary            = MosaicAmberAccent,
    onPrimary          = Color.White,
    primaryContainer   = MosaicAmberSecondary,
    onPrimaryContainer = MosaicInkLight,
    secondary          = MosaicAmberAccentDeep,
    onSecondary        = Color.White,
    tertiary           = MosaicEmerald,
    onTertiary         = Color.White,
    error              = MosaicCoral,
    onError            = Color.White,
    background         = MosaicCream,
    onBackground       = MosaicInkLight,
    surface            = MosaicCreamSurface,
    onSurface          = MosaicInkLight,
    surfaceVariant     = MosaicCreamMuted,
    onSurfaceVariant   = Color(0xFF52596B),
    outline            = MosaicBorderLight,
    outlineVariant     = MosaicBorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary            = MosaicAmberAccent,
    onPrimary          = MosaicNavy,
    primaryContainer   = MosaicAmberAccentDeep,
    onPrimaryContainer = MosaicCreamOnDark,
    secondary          = MosaicAmberSecondary,
    onSecondary        = MosaicNavy,
    tertiary           = MosaicEmerald,
    onTertiary         = Color.White,
    error              = MosaicCoral,
    onError            = MosaicNavy,
    background         = MosaicNavy,
    onBackground       = MosaicCreamOnDark,
    surface            = MosaicNavyTile,
    onSurface          = MosaicCreamOnDark,
    surfaceVariant     = MosaicNavySurfaceMuted,
    onSurfaceVariant   = Color(0xFFB1B4BB),
    outline            = MosaicBorderDark,
    outlineVariant     = MosaicBorderDark
)

// Rounded display typography to mirror iOS .rounded design.
// We don't ship a custom font — using FontFamily.Default keeps the APK
// small and the system rounded fallback matches "warm/personal" feel
// closer than monospace.
private val MosaicTypography = Typography(
    displayLarge   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,    fontSize = 36.sp),
    displayMedium  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,    fontSize = 28.sp),
    displaySmall   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,    fontSize = 24.sp),
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    headlineSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    titleMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    titleSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 13.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 16.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 14.sp),
    bodySmall      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 12.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
)

@Composable
fun MeritMosaicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = MosaicTypography,
        content     = content
    )
}
