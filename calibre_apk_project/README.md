# 📱 Calibre Reader - Android App

Android WebView app for accessing Calibre ebook library.

## Features
- WebView with Digest authentication support
- Credentials dialog with remember option
- Splash screen with Calibre branding
- Fullscreen reading mode
- Back button navigation
- Error handling and retry functionality
- Progress indicator

## Target
- **Calibre URL:** `http://100.112.100.14:7081/`
- **Authentication:** Digest auth
- **Package:** `com.calibre.reader`

## Build Instructions

### Automatic Build (GitHub Actions)
This repository is configured with GitHub Actions. On every push:
1. Android SDK is set up automatically
2. APK is built using Gradle
3. APK is available as artifact in Actions tab

### Manual Build
```bash
# Clone repository
git clone https://github.com/yourusername/calibre-reader-android.git
cd calibre-reader-android

# Build debug APK
cd calibre_apk_project
chmod +x gradlew
./gradlew assembleDebug

# APK location: app/build/outputs/apk/debug/app-debug.apk
```

### Requirements
- Android Studio (for manual build)
- Java JDK 11+
- Android SDK

## App Configuration

### Change Calibre URL
Edit: `app/src/main/java/com/calibre/reader/MainActivity.java`
```java
private static final String CALIBRE_URL = "http://your-new-url:port/";
```

### Change App Name
Edit: `app/src/main/res/values/strings.xml`
```xml
<string name="app_name">Your Custom Name</string>
```

## Authentication
- First launch shows credentials dialog
- Digest authentication handled automatically
- Credentials stored securely (if remember checked)
- Manual re-login if credentials fail

## Security Notes
- Uses HTTP (not HTTPS) - consider reverse proxy for production
- Digest auth is more secure than Basic auth
- Credentials stored with basic encryption
- Requires `android:usesCleartextTraffic="true"` for HTTP

## Compatibility
- **Min Android:** 8.0 (API 26)
- **Target Android:** 14 (API 34)
- **Permissions:** Internet, Network State
- **Screen orientation:** Both portrait and landscape

## Project Structure
```
calibre_apk_project/
├── app/
│   ├── src/main/java/com/calibre/reader/
│   │   ├── MainActivity.java          # Main app logic
│   │   └── DigestAuthWebViewClient.java # Authentication handler
│   ├── res/                           # Resources
│   └── AndroidManifest.xml           # App configuration
├── build.gradle                      # Build configuration
└── gradlew                          # Build script
```

## GitHub Actions
Workflow file: `.github/workflows/android.yml`
- Runs on: ubuntu-latest
- Builds: debug APK
- Uploads: APK as artifact
- Retention: 90 days

## Download APK
After GitHub Actions completes:
1. Go to "Actions" tab
2. Click on latest workflow run
3. Download "calibre-reader-apk" artifact
4. Install on Android device

## Troubleshooting
- **Build fails:** Check Android SDK setup
- **App crashes:** Verify Calibre server is running
- **Authentication fails:** Check credentials
- **Network errors:** Verify URL and network connection

## License
MIT License - see LICENSE file for details

---
**Auto-built by GitHub Actions** • **Calibre Reader v1.0.0**