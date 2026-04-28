// Color.kt — MeritMosaic brand palette (Android port).
//
// Mirrors iOS MMColor in MeritMosaic/Core/DesignSystem/Tokens.swift exactly:
// warm amber primary tiles glowing on a deep navy-teal ground, with a
// warm off-white background in light mode.
package com.katafract.meritmosaic.ui.theme

import androidx.compose.ui.graphics.Color

// Primary warm accent — the amber glow from the icon's lit tiles.
val MosaicAmberAccent      = Color(0xFFF4881C) // #F4881C
val MosaicAmberAccentDeep  = Color(0xFFD26210) // #D26210 — gradient bottom
val MosaicAmberSecondary   = Color(0xFFFBAE34) // #FBAE34 — brighter highlight

// Light scheme — warm off-white ground, deep navy text.
val MosaicCream            = Color(0xFFF9F7F3) // #F9F7F3 — light bg
val MosaicCreamMuted       = Color(0xFFF4F2ED) // #F4F2ED — surface muted
val MosaicCreamSurface     = Color(0xFFFFFFFF)
val MosaicInkLight         = Color(0xFF111B23) // #111B23 — text primary on cream

// Dark scheme — deep navy ground (matches icon background), cream text.
val MosaicNavy             = Color(0xFF111B23) // #111B23 — dark bg
val MosaicNavyTile         = Color(0xFF1B2832) // #1B2832 — surface
val MosaicNavySurfaceMuted = Color(0xFF233039) // #233039
val MosaicCreamOnDark      = Color(0xFFF9F7F3)

// Accent / utility colors used for status badges (coached, sharpen, pending).
val MosaicEmerald          = Color(0xFF10B982) // #10B982 — coached
val MosaicCoral            = Color(0xFFFF6B6B) // #FF6B6B — destructive

// Borders / dividers
val MosaicBorderLight      = Color(0xFFE4E1DB)
val MosaicBorderDark       = Color(0xFF283945)
