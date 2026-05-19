# Home Health Nurses — MVP

Compilable Android MVP using Firebase (Auth + Firestore) for registration and zip-code matching.

## File map

Drop these into a standard Android Studio project under `app/src/main/`:

```
src/main/java/com/example/homehealth/
    MyApplication.kt
    data/HealthRepository.kt
    location/LocationService.kt
    model/Models.kt
    ui/UserRegistrationActivity.kt
    ui/NurseRegistrationActivity.kt
    ui/NurseSearchActivity.kt
    ui/NursesAdapter.kt

src/main/res/layout/
    activity_user_registration.xml
    activity_nurse_registration.xml
    activity_nurse_search.xml
    list_item_nurse.xml

src/main/AndroidManifest.xml
```

The package is `com.example.homehealth` — change it consistently across all
files and the manifest if you want a different namespace.

## Gradle dependencies (`app/build.gradle.kts`)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Firebase
}

android {
    namespace = "com.example.homehealth"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    // Firebase BOM keeps Auth + Firestore versions in sync
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Play Services location (fused provider)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Coroutines + Tasks.await()
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
}
```

Root `build.gradle.kts` needs the Google Services plugin classpath:

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

## Firebase setup (one-time)

1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app with package name `com.example.homehealth`
3. Download `google-services.json` and drop it in `app/`
4. In the Firebase console:
   - **Authentication** → Sign-in method → enable **Email/Password**
   - **Firestore Database** → Create database → start in test mode (for the MVP)

## What's in the MVP

- **Two registration flows.** `UserRegistrationActivity` for people looking for
  a nurse, `NurseRegistrationActivity` for nurses offering services. Both use
  Firebase Auth (email/password) and write a profile document to Firestore.
- **Location service.** `LocationService` uses the Fused Location Provider to
  get the current location, then `Geocoder` to reverse-geocode it to a US zip
  code.
- **Zip-code matching.** `HealthRepository.findNursesByZip` runs a single
  Firestore query (`whereEqualTo("zipCode", zip)`) and returns the matching
  nurses.
- **Results list.** `NurseSearchActivity` shows results in a RecyclerView with
  Call and Email buttons wired to `Intent.ACTION_DIAL` / `ACTION_SENDTO`.

## What's deliberately out of scope (for the MVP)

- Login screen (only registration). Add one when you need returning users to
  re-authenticate.
- Real distinction between "user" and "nurse" accounts at sign-in time. Right
  now a single Firebase Auth account is created and a profile is written to
  the matching collection — that's enough for an MVP but you'll want a role
  flag (or separate auth providers) later.
- Radius search ("within 10 miles") — not native to Firestore. When you need
  this, look at Firebase's GeoFire library or a Cloud Function with geohashing.
- Firestore security rules. Test mode is fine while you're developing; lock
  it down before any real users arrive. The schema (uid-keyed documents)
  supports a rule like "users may only write their own uid document."
- Background location, push notifications, license verification, payments,
  ratings.

## Manual smoke test

1. Launch app → `UserRegistrationActivity` (the LAUNCHER activity).
2. Fill out the form, tap "Create account" → you should land on
   `NurseSearchActivity` with the zip you entered.
3. From a separate test device or by temporarily setting
   `NurseRegistrationActivity` as the launcher, register a nurse with the
   same zip code.
4. Back in `NurseSearchActivity`, tap Search — the nurse should appear.
5. Tap "Use my location" — grant permission — the zip field should populate
   and the search should re-run with your device's zip.
