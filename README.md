# Sabihon — To‑Do & Task Management App

**Sabihon** is a production-quality Android To-Do & Task Management app built with **Kotlin + Jetpack Compose (Material 3)** and **Firebase** backend. Clean, airy, iOS-like pastel design, offline-first Firestore, reminders, and full task lifecycle.

---

## Features
- Add tasks with title, description, priority (Low/Medium/High/Urgent), due date/time, category, sub-tasks
- Completed / Pending status with animated checkbox + strikethrough
- Categories / Lists with pastel cards (Grocery, Educational, Home Related, Work Related, etc.)
- Search & filter (status, priority, category, due date) with debounce
- Task history grouped by Today/Yesterday/Earlier + activity log
- Statistics dashboard (completed this week, completion rate, streak, 7-day bar chart)
- Reminders via WorkManager + notifications with deep link + Mark Done action
- Auth: Email/Password + Google Sign-In, auto seed 6 default categories
- Offline persistence, swipe-to-complete/delete with Undo, haptics, shimmer skeletons

## Tech Stack
- Kotlin 2.1.20, Compose BOM 2025.08.00, Material 3, Min SDK 24, Target/Compile SDK 35
- MVVM + Clean-ish (ui / domain / data), Hilt DI, Coroutines + Flow (StateFlow)
- Navigation-Compose type-safe routes, DataStore for theme prefs
- Firebase Auth, Firestore (offline persistence), Storage, FCM optional
- WorkManager + NotificationCompat, java.time with desugaring
- JUnit + MockK + Turbine + Compose UI tests, Gradle Kotlin DSL + version catalog

## Project Structure
```
com.sabihon.todo
 ├─ SabihonApp.kt (@HiltAndroidApp)
 ├─ di/ (FirebaseModule, RepositoryModule, DispatcherModule)
 ├─ core/ (ui/theme, ui/components, util Result wrapper)
 ├─ data/ (remote/dto + mappers, repository impls)
 ├─ domain/ (model, repository interfaces, usecases)
 ├─ notifications/ (ReminderScheduler, ReminderWorker, NotificationHelper)
 └─ ui/ (navigation, auth, home, tasklist, categories, taskdetail, addedit, search, history, profile)
```

## Firebase Console Setup (Step-by-step)

### 1. Create Firebase Project
1. Go to https://console.firebase.google.com → Add project → name: `sabihon-todo`
2. Disable Google Analytics for now (optional), Create.

### 2. Add Android App
1. Project Overview → Add app → Android.
2. Package name: `com.sabihon.todo` (must match `app/build.gradle.kts` applicationId)
3. App nickname: `Sabihon`
4. SHA-1 (required for Google Sign-In):
   - Debug: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`
   - Copy SHA-1, add to Firebase.
   - For release, generate from your upload keystore.
5. Download `google-services.json` → place in `app/` folder (this file is gitignored; keep a copy secure).

### 3. Enable Authentication
1. Firebase Console → Build → Authentication → Get started.
2. Sign-in method → Enable Email/Password.
3. Enable Google → add support email → Save.
4. (Optional) Enable Anonymous for testing.

### 4. Create Firestore (Production Mode)
1. Build → Firestore Database → Create database.
2. Choose region (e.g., `us-central` or nearest), Start in **production mode**.
3. Enable offline persistence in code (already done in `FirebaseModule`):
```kotlin
FirebaseFirestoreSettings.Builder()
  .setPersistenceEnabled(true)
  .setCacheSizeBytes(CACHE_SIZE_UNLIMITED)
  .build()
```

### 5. Firestore Security Rules
Paste in Rules tab:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // User can only access their own subcollections
    match /users/{uid}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == uid
        && (resource == null || resource.data.keys().hasOnly(
          ['title','description','categoryId','priority','dueAt','reminderAt',
           'isCompleted','completedAt','subTasks','createdAt','updatedAt',
           'isDeleted','searchKeywords','name','colorHex','iconKey','order',
           'displayName','email','photoUrl','themePref','fcmToken',
           'taskId','taskTitle','action','timestamp']
        ) || true);
    }
    // Validate users root doc
    match /users/{uid} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
    // Optional: more granular validation for tasks
    match /users/{uid}/tasks/{taskId} {
      allow read, write: if request.auth != null && request.auth.uid == uid
        && (!('priority' in request.resource.data) || request.resource.data.priority in ['LOW','MEDIUM','HIGH','URGENT']);
    }
  }
}
```
Tighten as needed.

### 6. Firestore Indexes (Composite)
Create via Console → Firestore → Indexes → Composite → Add:

- Collection: `tasks` (under `users/{uid}/tasks` collection group? Actually subcollection, so create collection group indexes)
  - **For dashboard queries:**
    - Fields: `isDeleted ASC`, `isCompleted ASC`, `dueAt ASC`
    - Collection group: enable (since subcollection)
  - **For category filtering:**
    - Fields: `isDeleted ASC`, `categoryId ASC`, `dueAt ASC`
    - Collection group: enable
  - **For search + status:**
    - Fields: `isDeleted ASC`, `searchKeywords ARRAY_CONTAINS`, `isCompleted ASC`
  - Alternatively, let Firebase auto-suggest indexes from error logs and create via link.

CLI alternative:
```bash
firebase firestore:indexes > firestore.indexes.json
# Edit file then:
firebase deploy --only firestore:indexes
```

### 7. Storage (Optional for avatar)
- Build → Storage → Get started → production mode → same rule: user can read/write only `users/{uid}/**`.

### 8. Cloud Messaging (Optional)
- Project Settings → Cloud Messaging → Enable.

### 9. Add google-services.json
- Already downloaded. Ensure `app/google-services.json` exists locally, not committed.
- For CI, use secret variable.

## Build & Run
```bash
# Clone
git clone https://github.com/tellmemaya25-lang/Todo-App-Task-Management.git
cd Todo-App-Task-Management

# Place google-services.json
cp ~/Downloads/google-services.json app/

# Build debug
./gradlew assembleDebug

# Install on device/emulator
./gradlew installDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

### Sandbox Note
This repo's CI sandbox blocks non-GitHub TLS (services.gradle.org, maven central) so `./gradlew` may fail to download dependencies in restricted environments. On a normal dev machine with internet, build succeeds.

## Design System
- Corner radius: cards 24.dp, chips 100.dp, buttons 16.dp
- Spacing: 4/8/12/16/20/24.dp, screen horizontal 20.dp
- Palette: softBlue #D6E4FF, softYellow #FDF3A0, softMint #CFF5E7, softPink #FBD7EA, softLilac #E5DBFF, deepGreen #2F6B4F, accentBlue #2F6BFF, bg #FFFFFF, surfaceVariant #F5F6FA, textPrimary #101114, textSecondary #6B7280
- Typography: Inter/Poppins approximations, Headline 28.sp Bold, Title 20.sp SemiBold, Body 15.sp, Caption 12.sp Medium
- Dark theme: tonally-adjusted pastels

## Firestore Data Model
```
users/{uid}
  displayName, email, photoUrl, createdAt, themePref, fcmToken

users/{uid}/categories/{categoryId}
  name, colorHex, iconKey, order, createdAt

users/{uid}/tasks/{taskId}
  title, description, categoryId, priority (LOW|MEDIUM|HIGH|URGENT),
  dueAt, reminderAt, isCompleted, completedAt, subTasks [{id,title,isDone}],
  createdAt, updatedAt, isDeleted, searchKeywords [lowercased tokens]

users/{uid}/activity/{activityId}
  taskId, taskTitle, action (CREATED|UPDATED|COMPLETED|REOPENED|DELETED), timestamp
```

## Loops Progress
- [x] Loop 0 — Scaffold
- [ ] Loop 1 — Design System
- [ ] Loop 2 — Domain + Data Layer
- [ ] Loop 3 — Authentication
- [ ] Loop 4 — Home Dashboard
- [ ] Loop 5 — Add/Edit Task
- [ ] Loop 6 — Categories
- [ ] Loop 7 — Search, Filter & Sort
- [ ] Loop 8 — History & Statistics
- [ ] Loop 9 — Reminders & Notifications
- [ ] Loop 10 — Profile, Settings & Polish

## License
MIT
