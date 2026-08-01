# AI For All — CI-buildable skeleton

Built for your Monday deadline: real navigation, real (if minimal) screens
for all 5 tabs + Community, and a GitHub Actions workflow that produces
an installable debug APK with no local Android Studio / Gradle needed.

## Get an APK today (fastest path)

1. Create a new GitHub repo, push everything in this zip to it (root of
   the repo = this folder's contents, so `settings.gradle.kts` sits at
   the repo root, not nested).
2. Go to the repo's **Actions** tab — the "Build Debug APK" workflow
   runs automatically on push to `main`, or click **Run workflow** to
   trigger it manually.
3. When it finishes (few minutes), open the workflow run → **Artifacts**
   → download `ai-for-all-debug-apk` → unzip → `app-debug.apk`.
4. Transfer that APK to your phone (or any Android device) and install
   it directly — no Play Store, no signing needed for a debug build.

This works **without Firebase configured yet** — the `google-services`
plugin only applies if `app/google-services.json` exists, so the app
builds and installs today with Community, Explore, Learn, etc. all
showing sample/placeholder data.

## Wiring up Firebase (do this before Community posts are real)

1. Create a Firebase project at console.firebase.google.com, add an
   Android app with package name `com.aiforall.app`.
2. Download the `google-services.json` it gives you.
3. `base64 -w0 google-services.json` (or any base64 tool) and paste the
   output as a repo secret named `GOOGLE_SERVICES_JSON` (Settings →
   Secrets and variables → Actions).
4. Push again / re-run the workflow — the build step now decodes that
   secret into `app/google-services.json` before compiling, and Firebase
   Auth/Firestore/Storage start working for real.
5. Enable Firestore + Storage in the Firebase console, and set security
   rules so posts only become publicly readable once `status ==
   "APPROVED"` — the app already writes new posts as PENDING_REVIEW and
   the feed query already filters to APPROVED, but Firestore rules are a
   separate, server-side enforcement layer you still need to add.

## Why Android 16 (API 36), not Android 17 (API 37)

Android 17 is out (June 2026, API level 37), but compiling against it
cleanly needs AGP 9.x, which is a bigger jump (different plugin
behavior, KSP over kapt in places) that I can't test-compile here since
this environment has no network access to actually run Gradle. Given
your deadline, I targeted **compileSdk/targetSdk 36 on AGP 8.7.3** —
that's the current Google Play minimum requirement anyway (mandatory
Aug 31, 2026) and a much safer bet to build clean on the first CI run.
minSdk is 26, comfortably under your Redmi 9A's Android 10 (API 29).
If you want API 37 specifically, say so and I'll do the AGP 9 migration
next — just flagging the tradeoff now rather than silently picking one.

## What's real vs placeholder right now

- **Real & wired:** navigation across all 6 destinations, theme
  (dark/glass/gradient), Community feed + create-post screen fully
  connected to Firestore via Hilt-injected `PostRepository`
- **Real UI, sample data:** Home, Explore (search + category filter +
  tool cards), Learn (5 tracks), Profile (section cards) — all render
  real layouts, just against hardcoded lists instead of a backend
- **Not yet built:** Firebase Auth (sign-in), Club's events/gallery/
  leaderboard/certificates sections beyond the Community entry point,
  Room offline caching, push notifications, app icon (placeholder
  vector mark — swap before any real release)

## Folder structure

```
app/src/main/java/com/aiforall/app/
├── AiForAllApp.kt / MainActivity.kt
├── di/                  — Hilt modules (Firebase, repositories)
├── domain/               — model + repository interfaces (no Firebase imports)
├── data/                 — Firestore-backed repository implementations
└── presentation/
    ├── theme/, components/  — shared GlassCard, GradientBackground, colors, type
    ├── navigation/           — BottomNavItem, Routes, NavGraph
    └── screens/              — home, explore, learn, club, profile, community
```
