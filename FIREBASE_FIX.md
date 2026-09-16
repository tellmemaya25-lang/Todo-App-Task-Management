# Firebase CONFIGURATION_NOT_FOUND Fix

Your build now succeeds (`assembleDebug` works, app installs). The Sign Up error:

```
An internal error has occurred. [ CONFIGURATION_NOT_FOUND ]
```

Means Firebase Auth SDK cannot find its backend config. 99% caused by Firebase Console setup, not code.

## Checklist – Do these in order

### 1. Enable Email/Password Auth
1. Go to https://console.firebase.google.com
2. Select your project (the one that matches your `google-services.json`)
3. Left menu → **Authentication** → **Get started** (if first time)
4. Tab **Sign-in method** → Enable **Email/Password** → Save
   - Enable both toggles if you see Email/Password and Email link

### 2. Verify Android app package name
1. Project Settings (gear icon) → **General** → **Your apps**
2. You must have an Android app with:
   - **Package name:** `com.sabihon.todo`  (exactly, no .debug suffix)
   - If you have `com.sabihon.todo.debug`, delete it or add new app with `com.sabihon.todo`
3. Check `google-services.json`:
   Open file in Notepad, find:
   ```json
   "package_name": "com.sabihon.todo"
   ```
   Must match. If it says `com.example...` or different, that's the bug.

### 3. Re-download google-services.json
**Important:** After enabling Auth, re-download the file.
1. Project Settings → Your apps → Android app → **Download google-services.json**
2. Replace file at:
   ```
   C:\Users\INTEL\OneDrive\Documents\School Projects (4rt Year)\Todo-App-Task-Management\app\google-services.json
   ```
3. **Do NOT** put in `app/src/debug/` – must be in `app/` root.

### 4. Check API Key not restricted
1. https://console.cloud.google.com/apis/credentials → select same project
2. Find **Android key** / **Browser key** → Edit
3. If "Application restrictions" = Android apps, add your SHA-1, or temporarily set to **None** for testing
4. If "API restrictions" → must allow **Identity Toolkit API** and **Token Service API**

### 5. Clean build
```powershell
cd "C:\Users\INTEL\OneDrive\Documents\School Projects (4rt Year)\Todo-App-Task-Management"
.\gradlew clean
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### 6. Test
- Sign Up with a new email (don't use gmial.com typo from screenshot – use gmail.com)
- Check Firebase Console → Authentication → Users – new user should appear
- Check Firestore → Data – `users/{uid}` doc should be created

## If still fails

Run logcat to see full error:
```powershell
adb logcat | Select-String -Pattern "FirebaseAuth|CONFIGURATION"
```

And verify in `app/google-services.json`:
- `project_id` matches Firebase project
- `client` array contains `client_info` with `package_name: com.sabihon.todo`
- `api_key` present

## Code fix pushed

I updated `AuthRepositoryImpl.friendlyMessage()` to detect this error and show actionable steps instead of raw "internal error". Pull latest:

```powershell
git pull origin arena/01a0a291-todo-app-task-management
```
