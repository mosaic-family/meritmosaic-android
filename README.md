# MeritMosaic Android

Android port of [`mosaic-family/meritmosaic-ios`](https://github.com/mosaic-family/meritmosaic-ios). Quiet-achiever journal app with AI clarify, credit-based pricing, and a personal-archive UX.

## Status

Initial port — feature-incomplete. See PR #1 for what's wired.

## Build

```bash
./gradlew assembleDebug                  # debug APK
SCREENSHOT_MODE=1 ./gradlew assembleDebug  # debug APK seeded with mock data
./gradlew bundleRelease                  # signed AAB (CI does this; needs keystore env vars)
```

## Stack

- Kotlin 2.0 + Compose BOM 2024.04
- Material 3 + edge-to-edge from day 1
- Ktor 2.3 client (HTTP + JSON), kotlinx.serialization
- DataStore (token persistence)
- Room (local cache, slot wired but not yet populated)
- Play Billing 7.1 (stubbed; full flow lands in follow-up PR)

## Architecture

```
app/src/main/java/com/katafract/meritmosaic/
├── api/              Ktor client → api.meritmosaic.io
├── data/             Models, AppConfig, MockDataSeeder
├── services/         AuthService (Sigil/Zitadel — currently stubbed)
├── viewmodel/        JournalVM, NewEntryVM, CreditsVM, SettingsVM
└── ui/
    ├── theme/        MeritMosaicTheme (warm amber on navy/cream)
    ├── components/   MosaicCard, MosaicTag, MosaicEmptyState
    ├── journal/      JournalScreen, ActivitiesScreen
    ├── newentry/     NewEntryScreen (Compose → Save → Coach → Refine → Done)
    ├── credits/      CreditStoreScreen
    └── settings/     SettingsScreen
```

## Brand

Mirrors iOS palette in `MMColor` (Core/DesignSystem/Tokens.swift): amber primary `#F4881C`, deep ember `#D26210`, warm cream `#F9F7F3`, deep navy `#111B23`. Rounded typography. Tab bar tinted amber.

## Screenshots / Play Store seed data

Set `SCREENSHOT_MODE=1` at build time. `MockDataSeeder` is the canonical curated content — keep voice consistent ("quiet achiever" — understated wins, real specifics, no humble-bragging).

## Published by

Katafract LLC.
