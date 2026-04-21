---
name: st226-android-avatar-maker
description: Analyze, modify, and debug the Android project ST226 Cute Anime Avatar Maker. Use when Codex works on this repo's avatar assembly flow, asset-driven body parts, online/local character data loading, background/text/sticker editor, Room avatar persistence, export/share flow, ads integration, or XML/view-binding screens tied to this project structure.
---

# ST226 Android Avatar Maker

Read [references/project-map.md](references/project-map.md) first.

## Work sequence

1. Identify which flow the request touches:
   - bootstrap/data load,
   - avatar customization,
   - background/text/sticker editor,
   - persistence/export/share,
   - settings/language/tutorial/permission,
   - ads or monetization wiring.
2. Read only the files on that path plus shared helpers they depend on.
3. Treat `app/src/main/assets` as part of the application logic, not passive media.
4. Preserve existing sentinel conventions:
   - `dice`
   - `none`
   - `icon` path with `x-y`
   - `thumb_*.png`
5. When editing avatar selection logic, verify both local data from `DataHelper.getData()` and online data hydration in `SplashActivity` or `MainActivity`.
6. When editing save/export behavior, trace the complete path from bitmap creation to Room persistence and next-screen navigation.

## Guardrails

- Do not rename or reshape `assets/data/*` without updating all readers.
- Do not change the ordering semantics of `arrInt`, `listImageSortView`, `listImage`, or `iconToIndexMap` casually.
- Do not remove ad hooks in activities unless the task explicitly asks for it.
- Do not introduce Compose, Flow rewrites, or architecture migrations unless explicitly requested.
- Prefer small compatible fixes over framework-level cleanup.

## File targeting

- For avatar/category/random issues, inspect:
  - `ui/customview`
  - `ui/category`
  - `ui/randomone`
  - `utils/DataHelper.kt`
  - `data/model/CustomModel.kt`
- For startup/data issues, inspect:
  - `ui/splash/SplashActivity.kt`
  - `ui/main/MainActivity.kt`
  - `data/callapi/*`
  - `utils/CONST.kt`
- For editor issues, inspect:
  - `ui/background/*`
  - `custom/*`
  - `dialog/DialogSpeech.kt`
  - adapters under `ui/background/adapter`
- For saved creations, inspect:
  - `data/room/*`
  - `data/repository/RoomRepository.kt`
  - `data/model/AvatarModel.kt`
  - `ui/my_creation/*`
- For share/export, inspect:
  - `utils/share/telegram/*`
  - `utils/share/whatsapp/*`
  - bitmap/file helpers in `utils`

## Expected output style

- Explain which flow is being changed.
- Call out any asset-structure assumptions explicitly.
- Mention if a change depends on online data shape or Android version behavior.
