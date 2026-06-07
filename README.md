# Hyper Reset Mobile

Android client for **Hyper Reset Performance** — a physical diagnosis and rehabilitation platform. Connects athletes (deportistas) with coaches for test-based performance tracking, appointment scheduling, and progress monitoring.

## Stack

| Layer | Technology |
|-------|-----------|
| Language | Java |
| Architecture | MVVM (Model-View-ViewModel) |
| Networking | Retrofit 2 + Gson |
| UI | Material Design 3 (XML layouts) |
| DI | Manual (AppContainer) |
| Auth | JWT (stored in SharedPreferences) |
| Navigation | Fragment-based with BottomNavigationView |
| Charts | Custom `WeeklyBarChartView` (Canvas) |

## Features

### Role-based navigation

**DEPORTISTA** — Home dashboard, Tests, Appointments, Messages, Profile
**COACH** — Home dashboard, Patients, Reports, Schedule, Messages, Profile

### Implemented screens

| Screen | Role | Status |
|--------|------|--------|
| **Splash / Onboarding** | All | ✅ 3-step intro |
| **Login / Register** | All | ✅ JWT auth |
| **Home Dashboard** | All | ✅ Charts, next appointment, weekly progress, achievements |
| **Edit Profile** | All | ✅ Update name, email, phone, address, birth date |
| **Change Password** | All | ✅ Current + new password with validation |
| **Settings** | All | ✅ Theme (light/dark/system), language (es/en), notifications toggle |
| **Tests List** | All | ✅ Role-aware (DEPORTISTA sees completed, COACH manages) |
| **Test Detail** | All | ✅ Results and scoring |
| **Appointments** | All | ✅ Create and view |
| **Messages** | All | ✅ Chat-style conversations with unread badges |
| **Patients (COACH)** | COACH | ✅ CRUD management |
| **Reports (COACH)** | COACH | ✅ Performance reports |
| **Activity Log (COACH)** | COACH | ✅ Paginated activity with infinite scroll |
| **Schedule (COACH)** | COACH | ✅ Appointments view |

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1+) or later
- Android SDK 34+
- Java 17+

### Setup

1. Clone the repo
2. Open with Android Studio
3. Configure the API base URL in `app/src/main/java/com/hyperreset/app/utils/Constants.java`:

```java
public static final String BASE_URL = "http://your-server:8080/api/";
```

4. For local development, update `res/xml/network_security_config.xml` to allow cleartext traffic to your server IP
5. Build and run

### Build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/src/main/java/com/hyperreset/app/
├── data/
│   ├── api/          # Retrofit ApiService + RetrofitClient
│   ├── local/        # Room database (placeholder)
│   ├── model/        # Data models (DTOs)
│   └── repository/   # Repository layer
├── di/               # AppContainer (manual DI)
├── ui/
│   ├── auth/         # Login, Register
│   ├── citas/        # Appointments (form, list)
│   ├── custom/       # Custom views (GradientCardView, WeeklyBarChartView)
│   ├── dashboard/    # Activity list with pagination
│   ├── deportistas/  # Patient CRUD (detail, form, list)
│   ├── home/         # Dashboard, ViewModel
│   ├── materiales/   # Training materials
│   ├── mensajes/     # Messaging (conversation, form, list)
│   ├── perfil/       # Profile (edit, change password, settings)
│   ├── reportes/     # Reports
│   ├── splash/       # Onboarding
│   └── tests/        # Tests (create, detail, list)
└── utils/            # Constants, Resource, SessionManager, SettingsManager

app/src/main/res/
├── drawable/         # Icons, shapes, gradients
├── layout/           # XML layouts
├── menu/             # Bottom navigation menus
├── values/           # Colors, strings, themes
└── values-en/        # English translations
```

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit 2 | 2.9.0 | HTTP client |
| OkHttp | 4.12.0 | HTTP engine + logging |
| Gson | 2.11.0 | JSON serialization |
| Material Components | 1.12.0 | Material Design 3 |
| AndroidX Lifecycle | 2.10.0 | ViewModel + LiveData |
| AndroidX ViewPager | 1.0.0 | Onboarding carousel |
| SwipeRefreshLayout | 1.1.0 | Pull-to-refresh |

## Session handling

- JWT token stored in SharedPreferences via `SessionManager`
- On 401 response → global `SessionExpiredListener` clears session and redirects to Login
- Token is validated on splash screen before navigating to Home

## License

MIT
