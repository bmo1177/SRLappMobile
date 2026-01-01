# Firebase Setup Guide for Android

This guide explains how to fix the `google-services.json is missing` error and configure Firebase for the SRL App.

## 1. Create a Firebase Project
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add project** and follow the steps to create a new project (e.g., "SRL-App").

## 2. Register the Android App
1. In the Firebase console, click the **Android icon** to add an app.
2. Enter the Package Name: `com.example.srlappexperiment`.
3. (Optional) Enter an App Nickname.
4. **Important**: For Google Sign-In, you MUST provide your **Debug signing certificate SHA-1**.
   - Run this command in your terminal to get the SHA-1:
     ```bash
     ./gradlew signingReport
     ```
   - Look for the `SHA1` value under the `debug` variant.

## 3. Download google-services.json
1. After registering, click **Download google-services.json**.
2. Move this file into the `app/` directory of your project:
   - Path: `/home/dev-lab/AndroidStudioProjects/SRLAppExperiment/app/google-services.json`

## 4. Enable Authentication Providers
1. In the Firebase console, go to **Build > Authentication**.
2. Click **Get Started**.
3. Under the **Sign-in method** tab, enable:
   - **Email/Password**
   - **Google** (Configure the web SDK ID which Firebase provides automatically when you enable this).

## 5. Setup Firestore Database
1. Go to **Build > Firestore Database**.
2. Click **Create database**.
3. Choose **Start in test mode** for development (or set production rules).
4. Select a location near you.

## 6. Verify Google Sign-In Requirements
- Ensure your `build.gradle.kts` (app level) has the plugin:
  ```kotlin
  plugins {
      alias(libs.plugins.google.services)
  }
  ```
- Ensure the project-level `build.gradle.kts` has the dependency:
  ```kotlin
  plugins {
      alias(libs.plugins.google.services) apply false
  }
  ```

Once `google-services.json` is in place, the project should compile successfully!
