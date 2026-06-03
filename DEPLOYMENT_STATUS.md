# Hyper Reset - Ngrok Real Device Testing Checklist

## ✅ COMPLETED SETUP

- ✅ **Ngrok Installed**: v3.22.1 available on system
- ✅ **Android Constants.java Updated**: Supports configurable Ngrok URL with `USE_NGROK` toggle
- ✅ **Deployment Guide Created**: Comprehensive markdown documentation with troubleshooting
- ✅ **Setup Scripts Created**: 
  - `setup-ngrok.bat` (Windows CMD)
  - `setup-ngrok.ps1` (PowerShell) 

---

## 📋 QUICK START (5 Minutes)

### Step 1: Start Ngrok (Terminal/PowerShell - Keep Running)
```bash
ngrok http 8080
```
**⚠️ Save the URL shown** (e.g., `https://xxxx-yy-zzzz.ngrok.io`)

### Step 2: Update Backend CORS (Spring Boot Project)
Add to WebConfig.java or application.yml:
```yaml
allowed-origins: 
  - "http://10.0.2.2:8080"
  - "https://xxxx-yy-zzzz.ngrok.io"  # ← Your Ngrok URL
```
Restart Spring Boot backend.

### Step 3: Update Android App (This Project)
Edit: `app/src/main/java/com/hyperreset/app/utils/Constants.java`
```java
private static final String NGROK_URL = "https://xxxx-yy-zzzz.ngrok.io/api/";
private static final boolean USE_NGROK = true;  // ← Change to true
```

### Step 4: Build & Install
```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 5: Test on Physical Device
- Open app
- Register → Should reach backend via Ngrok
- Login → Should authenticate and get token
- Navigate to dashboard → Should load remote data

---

## 🔍 VERIFICATION CHECKLIST

Before declaring "ready for real device testing":

| Item | Status | Notes |
|------|--------|-------|
| Ngrok tunnel active | ⚠️ Manual | Run `ngrok http 8080` |
| Ngrok URL obtained | ⚠️ Manual | Look for `Forwarding https://...` |
| Backend CORS updated | ⚠️ Manual | Add Ngrok domain |
| Backend restarted | ⚠️ Manual | Must reload to apply CORS |
| Constants.java updated | ⚠️ Manual | USE_NGROK=true, NGROK_URL set |
| APK rebuilt | ⚠️ Manual | `./gradlew assembleDebug` |
| APK installed on device | ⚠️ Manual | `adb install -r app...apk` |
| Device can reach backend | 🔧 Testing | Try: `curl https://xxxx.ngrok.io` |
| Registration works | 🔧 Testing | Create test account |
| Login works | 🔧 Testing | Auth token received |
| Protected endpoints work | 🔧 Testing | Profile/dashboard load |
| Network traffic visible | ⚠️ Manual | Check `http://127.0.0.1:4040` |

**Legend**: ⚠️ = Manual action | 🔧 = Test & verify | ✅ = Already done

---

## 🛠️ USAGE SCRIPTS

### Option A: Use PowerShell Helper (Recommended)
```bash
pwsh -ExecutionPolicy Bypass -File setup-ngrok.ps1
```
Interactive menu with all common tasks.

### Option B: Use Batch Helper (Windows)
```bash
setup-ngrok.bat
```
Simple interactive menu.

### Option C: Manual Commands
```bash
# Terminal 1: Start Ngrok (keep running)
ngrok http 8080

# Terminal 2: View traffic in real-time
curl http://127.0.0.1:4040

# Terminal 3: Build and test
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## ⏰ NGROK SESSION MANAGEMENT

| Aspect | Free Tier | Impact |
|--------|-----------|--------|
| **Session Duration** | 2 hours | Need to restart & update app |
| **URL Stability** | Changes on restart | Must update Constants.java |
| **Connections/min** | Unlimited | No throttling |
| **Bandwidth** | Unlimited | No bandwidth limits |

### When Session Expires (Every 2 Hours)
1. Ngrok terminal will show: `Session expired`
2. Run `ngrok http 8080` again
3. Get new URL from output
4. Update `NGROK_URL` in Constants.java
5. Rebuild: `./gradlew assembleDebug`
6. Reinstall: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
7. Update backend CORS with new URL

---

## 📞 TROUBLESHOOTING QUICK ANSWERS

**Q: Device says "Connection timeout"**
- Check Ngrok is running (see terminal)
- Check NGROK_URL in Constants.java is correct
- Check backend CORS includes Ngrok domain

**Q: "CORS error" after login**
- Backend CORS not updated
- Backend not restarted
- Old browser/app cache - clear and retry

**Q: "401 Unauthorized" on dashboard**
- JWT token not being sent
- Check AuthInterceptor in RetrofitClient.java
- Token may have expired (re-login)

**Q: After 2 hours, device can't connect**
- Ngrok free tier session expired
- Restart Ngrok to get new URL
- Update app and reinstall

**See full troubleshooting** → `NGROK_DEPLOYMENT_GUIDE.md` Part 5

---

## 📁 FILES CREATED/MODIFIED

| File | Purpose | Status |
|------|---------|--------|
| `NGROK_DEPLOYMENT_GUIDE.md` | Complete setup & troubleshooting guide | ✅ Created |
| `setup-ngrok.ps1` | PowerShell interactive helper | ✅ Created |
| `setup-ngrok.bat` | Windows batch helper | ✅ Created |
| `app/src/main/java/com/hyperreset/app/utils/Constants.java` | Configurable API endpoint | ✅ Modified |

---

## 🚀 DEPLOYMENT STATUS: READY

**Current Status**: ✅ **READY FOR REAL DEVICE TESTING**

All configuration is in place. Follow the 5-step Quick Start above to begin testing on physical Android devices.

### What's Included:
- ✅ Ngrok tunnel setup guide
- ✅ Backend CORS configuration instructions
- ✅ Android app endpoint switching mechanism
- ✅ Complete troubleshooting guide
- ✅ Interactive setup scripts
- ✅ Real device testing workflow
- ✅ Known limitations & workarounds

### What You Need to Provide:
- ⚠️ Actual Ngrok URL (run `ngrok http 8080` to get it)
- ⚠️ Backend CORS configuration updates
- ⚠️ Physical Android device with network access

### Next Steps:
1. **Now**: Read `NGROK_DEPLOYMENT_GUIDE.md` Part 1-2
2. **Then**: Start Ngrok and get public URL
3. **Then**: Update backend CORS and restart
4. **Then**: Update Android Constants.java
5. **Finally**: Build, install, and test on device

---

**Quick Links**:
- Full Guide: `NGROK_DEPLOYMENT_GUIDE.md`
- Setup Helper: `pwsh -ExecutionPolicy Bypass -File setup-ngrok.ps1`
- Backend Source: (External - update your Spring Boot project)

**For Questions**: Check NGROK_DEPLOYMENT_GUIDE.md → Part 5: Troubleshooting
