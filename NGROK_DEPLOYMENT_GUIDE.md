# Ngrok Deployment Guide - Hyper Reset Real Device Testing

## Overview
This guide helps you expose the Hyper Reset Spring Boot backend (localhost:8080) to the internet via Ngrok tunnel so physical Android devices can access it for testing.

---

## Part 1: Ngrok Installation & Tunnel Setup

### 1.1 Verify Ngrok Installation
Ngrok is already installed on your system.

```bash
ngrok --version
# Output: ngrok version 3.22.1
```

### 1.2 Start Ngrok Tunnel
Open a terminal/PowerShell window and run:

```bash
ngrok http 8080
```

**Expected Output:**
```
Session Status                online
Account                       [your-account]
Version                        3.22.1
Region                         us (United States)
Latency                        41ms
Web Interface                  http://127.0.0.1:4040
Forwarding                     https://xxxx-yy-zzzz.ngrok.io -> http://localhost:8080
Forwarding                     http://xxxx-yy-zzzz.ngrok.io -> http://localhost:8080

Connections                    ttl    opn    tot    tls
                               0      0      0      100%
```

**Key Information:**
- **Public HTTPS URL**: `https://xxxx-yy-zzzz.ngrok.io` (save this!)
- **Web Inspector**: `http://127.0.0.1:4040` (view traffic locally)
- **Duration**: Free tier = 2 hours per session (you'll get a new URL when it expires)

### 1.3 Keep Ngrok Running
Leave this terminal window open while testing. The tunnel must stay active for devices to reach your backend.

### 1.4 Free Tier Limitations
| Feature | Limit |
|---------|-------|
| Session Duration | 2 hours |
| Requests/min | Unlimited |
| Bandwidth | Unlimited |
| Custom Domain | ❌ (paid plans only) |
| TCP/UDP | ❌ (paid plans only) |

**Workaround for Session Expiry**: When 2 hours passes, run `ngrok http 8080` again, get new URL, update app config, and rebuild.

---

## Part 2: Android Configuration Changes

### 2.1 Update Constants.java (ALREADY DONE ✓)
File: `app/src/main/java/com/hyperreset/app/utils/Constants.java`

**What was changed:**
```java
// Before: Single hardcoded URL
public static final String BASE_URL = "http://10.0.2.2:8080/api/";

// After: Configurable with switch
private static final String NGROK_URL = "https://xxxx-yy-zzzz.ngrok.io/api/";
private static final String LOCALHOST_URL = "http://10.0.2.2:8080/api/";
private static final boolean USE_NGROK = false;
public static final String BASE_URL = USE_NGROK ? NGROK_URL : LOCALHOST_URL;
```

### 2.2 Switch to Ngrok (When Ready to Test)
In `Constants.java`, replace the Ngrok URL and toggle the flag:

```java
// Step 1: Get your public URL from ngrok terminal output
// Example: https://1a2b-34-567c-8d9ef-10g.ngrok.io

// Step 2: Update NGROK_URL
private static final String NGROK_URL = "https://1a2b-34-567c-8d9ef-10g.ngrok.io/api/";

// Step 3: Enable Ngrok
private static final boolean USE_NGROK = true;  // ← Change this to true
```

### 2.3 Rebuild the APK
```bash
# In Android Studio or via terminal:
./gradlew assembleDebug

# Or for release:
./gradlew assembleRelease
```

---

## Part 3: Backend Configuration (Spring Boot)

### 3.1 CORS Configuration Update
Your Spring Boot backend's CORS config currently allows only emulator:
```java
.allowedOrigins("http://10.0.2.2:8080")
```

**Action Required**: Add your Ngrok domain to CORS allowed origins.

**Example (if using WebConfig.java):**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://10.0.2.2:8080",           // Emulator
                    "https://1a2b-34-567c-8d9ef-10g.ngrok.io"  // Replace with YOUR Ngrok URL
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}
```

**Example (if using application.yml):**
```yaml
spring:
  web:
    cors:
      allowed-origins: 
        - "http://10.0.2.2:8080"
        - "https://1a2b-34-567c-8d9ef-10g.ngrok.io"
      allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
      allowed-headers: "*"
      allow-credentials: true
```

### 3.2 JWT Token Configuration
✓ **No changes needed** - JWT tokens are created server-side and only contain user identity, not origin information.

### 3.3 Restart Spring Boot Backend
After updating CORS:
```bash
# Stop the running backend
# Restart it (Maven, Gradle, or IDE run button)
```

---

## Part 4: Real Device Testing Workflow

### 4.1 Prerequisites
- Physical Android device with:
  - WiFi enabled
  - Same network as your development machine (recommended) OR public internet access
  - Android 6.0+ installed
- Backend running on `localhost:8080`
- Ngrok tunnel active and providing public URL
- Android app built with Ngrok URL and `USE_NGROK = true`

### 4.2 Step-by-Step Testing Process

#### Step 1: Start Ngrok Tunnel
```bash
ngrok http 8080
```
Note the public URL (e.g., `https://xxxx-yy-zzzz.ngrok.io`)

#### Step 2: Update Backend CORS
Add Ngrok URL to allowed origins and restart Spring Boot backend.

#### Step 3: Update Android App
- Edit `Constants.java`
- Set `NGROK_URL = "https://xxxx-yy-zzzz.ngrok.io/api/"`
- Set `USE_NGROK = true`
- Rebuild APK: `./gradlew assembleDebug`

#### Step 4: Install APK on Device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### Step 5: Test Authentication Endpoints
**Register Test:**
1. Open app on physical device
2. Go to Registration screen
3. Enter test credentials:
   - Email: `testdev@example.com`
   - Password: `TestPassword123`
   - Full Name: `Test Developer`
4. Tap Register button
5. Check result:
   - ✓ Success: Welcome screen appears, token stored
   - ❌ Error: See troubleshooting below

**Login Test:**
1. Go to Login screen
2. Enter credentials (from registration)
3. Tap Login
4. Verify:
   - ✓ Success: Dashboard loads
   - ❌ Error: See troubleshooting below

#### Step 6: Test Protected Endpoints
Once logged in, test fetching data:
- Open Dashboard → Check if sportspeople/athletes list loads
- Open Profile → Check if profile data loads
- Try to create/edit a resource (if endpoints available)

#### Step 7: Monitor Network Traffic
While testing on device, view live HTTP traffic:
```
Open: http://127.0.0.1:4040
```
This shows all requests routed through Ngrok tunnel.

---

## Part 5: Troubleshooting

### Problem: Device Cannot Reach Backend

**Symptoms:**
- Connection timeout errors
- "Could not resolve hostname"
- ERR_NAME_NOT_RESOLVED

**Solutions:**
1. **Verify Ngrok is running**
   ```bash
   # Check terminal where you ran ngrok http 8080
   # Should see "Session Status: online"
   ```

2. **Verify Ngrok URL is correct in Constants.java**
   ```java
   // This must match exactly what Ngrok shows
   private static final String NGROK_URL = "https://xxxx-yy-zzzz.ngrok.io/api/";
   ```

3. **Check device network connectivity**
   ```bash
   # On physical device, open browser and visit:
   https://xxxx-yy-zzzz.ngrok.io
   # You should see "Tunnel not running: backend error"
   # (This is expected - it proves DNS works)
   ```

4. **Restart Ngrok if >2 hours passed**
   - Free tier sessions expire after 2 hours
   - Ngrok will show: "Session expired"
   - Get new URL and update Android app

### Problem: 403 Forbidden / CORS Error

**Symptoms:**
```
Access to XMLHttpRequest at 'https://xxxx.ngrok.io/api/...' from origin 'your-device-ip'
has been blocked by CORS policy
```

**Solution:**
1. Add Ngrok URL to backend CORS allowed origins
2. Verify backend was restarted (not just recompiled)
3. Clear browser/app cache and try again

### Problem: 401 Unauthorized (After Login Works)

**Symptoms:**
- Login successful but subsequent requests fail
- Dashboard shows "Unauthorized"

**Possible Causes:**
1. **JWT token not being sent** - Check RetrofitClient.java AuthInterceptor:
   ```java
   // Should add "Authorization: Bearer {token}" header
   .header("Authorization", "Bearer " + authToken);
   ```

2. **Token expired** - Tokens have expiry time:
   - Default: Usually 15 min - 1 hour
   - Solution: Re-login or extend token expiry in backend

3. **Backend can't validate token** - Check secret key:
   - Ensure both frontend and backend use same JWT secret
   - Check application.yml: `jwt.secret` property

### Problem: Ngrok Session Expires (2 hours)

**Symptoms:**
- After 2 hours, Ngrok shows: "Tunnel expired"
- Device shows "Connection refused"

**Solution:**
```bash
# 1. Restart Ngrok
ngrok http 8080

# 2. Get new URL from terminal output

# 3. Update Android Constants.java with new URL
private static final String NGROK_URL = "https://NEW-URL.ngrok.io/api/";

# 4. Rebuild app: ./gradlew assembleDebug

# 5. Reinstall on device: adb install -r app/build/outputs/apk/debug/app-debug.apk

# 6. Update backend CORS with new URL and restart Spring Boot
```

---

## Part 6: Testing Checklist

Use this checklist before marking deployment as "ready":

### Ngrok Setup ✓
- [ ] Ngrok running: `ngrok http 8080`
- [ ] Public HTTPS URL obtained
- [ ] Web inspector at `http://127.0.0.1:4040` responsive

### Backend Configuration ✓
- [ ] Spring Boot running on `localhost:8080`
- [ ] CORS updated with Ngrok domain
- [ ] Backend restarted after CORS change
- [ ] Database/auth endpoints working locally (test with Postman)

### Android Configuration ✓
- [ ] `Constants.java` updated with Ngrok URL
- [ ] `USE_NGROK = true`
- [ ] APK rebuilt: `./gradlew assembleDebug`
- [ ] No build errors

### Device Testing ✓
- [ ] APK installed on physical device
- [ ] Device connected to network with internet
- [ ] App opens without crashes
- [ ] **Auth Test**: Registration succeeds
- [ ] **Auth Test**: Login succeeds with registered credentials
- [ ] **API Test**: Dashboard loads (if protected endpoint)
- [ ] **API Test**: Profile loads (if protected endpoint)
- [ ] **Network Test**: Ngrok Web Inspector shows requests in real-time
- [ ] **Error Test**: Disconnect device internet → see error message
- [ ] **Error Test**: Reconnect → requests retry/succeed

### Regression Testing ✓
- [ ] Switch `USE_NGROK = false`
- [ ] Rebuild APK
- [ ] Test on Android Emulator still works
- [ ] Auth and basic endpoints work on emulator

---

## Part 7: Known Limitations & Workarounds

| Issue | Limitation | Workaround |
|-------|-----------|-----------|
| **Session Duration** | 2 hours max | Restart Ngrok and update app config |
| **Custom Domain** | Not available (free) | Use dynamic URL or upgrade to paid |
| **Bandwidth** | Unlimited* | Monitor via dashboard |
| **Concurrent Sessions** | 1 per account (free) | Pay for upgrade or restart tunnel |
| **Latency** | 40-100ms | Expected; use for dev/testing, not production |
| **SSL Certificate** | Auto-generated | Browser may warn; it's safe for testing |

---

## Part 8: Production Notes (Future)

When you're ready to move to production:

1. **Don't use Ngrok** - Deploy to actual server with proper domain
2. **Consider alternatives**:
   - AWS EC2 + Elastic IP
   - DigitalOcean Droplet
   - Heroku (deprecated, but similar)
   - Firebase Cloud Functions
3. **Update backend**:
   - Real SSL certificate (Let's Encrypt)
   - Hardened CORS (specific origins only)
   - Rate limiting
   - API authentication/API keys
4. **Update Android app**:
   - BuildConfig variants for dev/staging/prod
   - Remove hardcoded URLs

---

## Quick Reference Commands

```bash
# Start Ngrok tunnel
ngrok http 8080

# View Ngrok web inspector
curl http://127.0.0.1:4040

# Build Android APK
./gradlew assembleDebug

# Install on device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# View device logs (for debugging)
adb logcat | grep -i "hyperreset\|retrofit\|okhttp"

# Stop Ngrok (in the terminal running it)
Ctrl+C
```

---

## Support Resources

- **Ngrok Docs**: https://ngrok.com/docs
- **Ngrok Web Inspector**: http://127.0.0.1:4040 (when running)
- **Retrofit Android**: https://square.github.io/retrofit/
- **Spring Boot CORS**: https://spring.io/guides/gs/rest-service-cors/

---

**Document Version**: 1.0  
**Last Updated**: May 31, 2026  
**Status**: Ready for Real Device Testing
