# ST226 Cute Anime Avatar Maker Rule

Apply this rule when working in `ST226_CuteAnimeAvatarMaker`.

## Goals

- Preserve the current product flow: `Splash -> Main -> Category/Random/Cosplay -> Customview -> Background -> Success/MyCreation`.
- Prefer minimal fixes that keep UI behavior, ad flow, export/share behavior, and asset conventions stable.

## Required constraints

- Treat `app/src/main/assets/data`, `assets/bg`, `assets/BG_Text`, and `assets/sticker` as application data, not passive media. Do not rename folders, `nav.png`, `thumb_*.png`, color subfolders, or layer ordering without tracing `DataHelper` end to end.
- When changing avatar logic, verify the interaction between `DataHelper`, `SplashActivity` or `MainActivity`, and `CustomviewActivity`. The app mixes local and online data, and index shifts around `none` and `dice` are easy to break.
- Preserve the `icon` naming convention with `x-y`. `CustomviewActivity` depends on `listImageSortView`, `listImage`, `iconToIndexMap`, and `arrInt` to build render order.
- If adding body parts, colors, or online assets, keep them compatible with `BodyPartModel`, `ColorModel`, and `CustomModel`, including the sentinel insertion rules:
  - `*-1` layers start with `dice`.
  - other layers start with `none`, then `dice`, then real assets.
- Keep `AvatarModel` backward compatible unless you also add a Room migration. The current schema stores `path`, `pathAvatar`, `online`, `arr`, and `isFlipped`.
- Do not break Hilt wiring in `App`, `ApplicationModule`, `RoomRepository`, or `ApiRepository`.
- Do not change package names, `FileProvider` authority, `StickerContentProvider` authority, or ads/firebase keys unless explicitly requested.
- For UI work, stay within the current XML, view binding, and data binding approach. Do not migrate the project to Compose unless asked.
- For save/share/export changes, trace `saveBitmap`, `viewToBitmap`, `BackgroundActivity`, `SuccessActivity`, `WhatsappSharingActivity`, and Telegram sharing helpers.
- Be careful with permission or storage changes. The app targets SDK 36 but still carries older storage patterns such as `WRITE_EXTERNAL_STORAGE`.

## Preferred working style

- Read real source under `app/src/main/java` and `app/src/main/res`; ignore `app/build`.
- When debugging avatar rendering, check:
  - asset or online file presence,
  - color and part indices in `arrInt`,
  - thumbnail and real-image list alignment,
  - Glide reuse via `tag` and cache behavior.
- When debugging the background/text/sticker editor, inspect `BackgroundActivity`, `BackGroundViewModel`, `DrawView`, related dialogs, and default lists from `DataHelper`.
- Before large edits, state the main risks briefly: asset shape, layer order, ad callbacks, export path, or online data assumptions.
