# Project Map

## Stack

- Android app, 1 module: `app`
- Kotlin + a few Java files
- View Binding + Data Binding
- Hilt for DI
- Room for saved avatar metadata
- Retrofit/OkHttp for remote character data
- Glide for image loading
- LVT Ads library + AdMob mediation
- Firebase Analytics, Crashlytics, Messaging, Remote Config

## High-value entry points

- `app/src/main/java/com/cute/anime/avatarmaker/App.kt`
  - app class, app-open ads, music foreground/background lifecycle
- `app/src/main/java/com/cute/anime/avatarmaker/ui/splash/SplashActivity.kt`
  - initial data load, splash ad timing, early online data hydration, first-image preloading
- `app/src/main/java/com/cute/anime/avatarmaker/ui/main/MainActivity.kt`
  - home navigation, network receiver, another online hydration path
- `app/src/main/java/com/cute/anime/avatarmaker/utils/DataHelper.kt`
  - central local asset parsing, default lists, shared mutable state

## Core data model

- `CustomModel`
  - one character/category pack
- `BodyPartModel`
  - one avatar layer with `icon`, color groups, thumbs, single-path scratch list
- `ColorModel`
  - one color group containing real image paths
- `AvatarModel`
  - Room entity for saved work

## Local asset conventions

- `assets/data/<character>/<part>/...`
- part folders use `x-y` naming encoded in `icon` path
- `nav.png` is the navigation icon for a body part
- `thumb_*.png` is thumbnail data when present
- colored variants live in nested color folders
- some flat folders are split into real images and thumbs in code
- background assets live in `assets/bg`
- speech bubble assets live in `assets/BG_Text`
- sticker assets live in `assets/sticker`

## Avatar assembly flow

1. `DataHelper.getData()` parses local assets into `arrBlackCentered`.
2. `SplashActivity` and `MainActivity` optionally prepend online characters from API.
3. `CategoryActivity` chooses character pack.
4. `CustomviewActivity` builds the layered avatar UI.
5. `arrInt` stores selected part index and color index per layer.
6. The customized bitmap is saved, then `BackgroundActivity` adds background, text, stickers.
7. Final bitmap is exported and shown in success screens.

## Fragile logic to respect

- `none` and `dice` are injected into part lists and shift indices.
- `arrInt` is persisted as JSON string by `fromList(...)`.
- `listImageSortView`, `listImage`, and `iconToIndexMap` determine actual render order.
- Online and local data are normalized separately and are not fully symmetric.
- `CustomviewActivity` uses `view.tag` plus Glide caching to avoid redundant loads.

## Persistence and export

- Room database stores only avatar metadata, not all rendered layers as separate rows.
- Save flow relies on bitmap helpers under `utils` and then routes into background or success screens.
- WhatsApp and Telegram sharing are implemented under `utils/share`.

## Practical debugging hints

- Missing part image usually means one of:
  - wrong asset folder shape,
  - bad `x-y` mapping,
  - sentinel index shift after inserting `none` or `dice`,
  - thumb list and real list length mismatch,
  - stale Glide state on reused `ImageView`.
- If startup data seems duplicated or inconsistent, compare the online hydration code in `SplashActivity` and `MainActivity`.
- If saved creations break, inspect `AvatarModel`, `RoomRepository`, and any code that reconstructs `arr` back into indices.
