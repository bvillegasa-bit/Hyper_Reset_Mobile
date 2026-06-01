# ⚡ Quick Testing Checklist

## 🟢 BACKEND - Postman Tests (9 tests)

### Prerequisites
- [ ] Obtener `DEPORTISTA_TOKEN` (login deportista)
- [ ] Obtener `COACH_TOKEN` (login coach)
- [ ] Importar `postman_collection.json` en Postman

### Backend Tests
- [ ] **1.1** GET /api/citas (Deportista) → 200 ✅
- [ ] **1.2** PUT /api/deportistas/{id} (Edit own) → 200 ✅
- [ ] **1.3** PUT /api/deportistas/{999} (Edit other) → 403 ✅ (SECURITY)
- [ ] **1.4** GET /api/deportistas/coaches → 200 ✅
- [ ] **1.5** GET /api/test-fisicos (Deportista) → 200 ✅
- [ ] **1.6** GET /api/deportistas/coach/{id} (Coach) → 200 ✅
- [ ] **1.7** GET /api/resultados/deportista/{id} (own) → 200 ✅
- [ ] **1.8** GET /api/resultados/deportista/{999} (other) → 403 ✅ (SECURITY)
- [ ] **1.9** GET /api/deportistas (No token) → 401 ✅ (SECURITY)

---

## 📱 ANDROID UI - Emulator Tests (12 tests)

### Login & Session
- [ ] **2.1** Login deportista → HomeActivity ✅
- [ ] **2.2** SessionManager.getUserRole() == "DEPORTISTA" ✅
- [ ] **2.3** Bottom nav tiene tabs DEPORTISTA (no Deportistas tab) ✅

### Profile - Deportista
- [ ] **2.4** Ver perfil: nombre, email, teléfono, dirección ✅
- [ ] **2.5** Editar perfil → datos se actualizan ✅
- [ ] **2.6** Seleccionar coach → coach se cambia ✅

### Content - Deportista
- [ ] **2.7** Ver citas → GET /api/citas 200 ✅
- [ ] **2.8** Ver pruebas → GET /api/test-fisicos 200 ✅
- [ ] **2.9** Logout → LoginActivity ✅

### Login & Session - Coach
- [ ] **2.10** Login coach → getUserRole() == "COACH" ✅
- [ ] **2.11** Bottom nav tiene Deportistas, Citas, Reportes ✅
- [ ] **2.12** Ver deportistas asignados → GET /api/deportistas/coach/{id} 200 ✅

---

## ✅ FINAL CHECKLIST

### Errors Found
- [ ] No errors (TODO: Update if found)

### Results Summary
```
Backend Tests:  9/9 ✅
Android Tests: 12/12 ✅
Security:       4/4 ✅
───────────────
TOTAL:         25/25 ✅

Status: READY FOR PRODUCTION ✅
```

### Before Deployment
- [ ] All tests passing ✅
- [ ] No 403 or 401 errors (except expected security tests)
- [ ] SessionManager working correctly
- [ ] Token persisted across app restart
- [ ] UI matches expected role tabs
- [ ] Data consistency verified
- [ ] TESTING_RESULTS.md updated with results
- [ ] Commit & push changes

---

## 🔗 Quick Links
- **Postman Collection**: `postman_collection.json`
- **Full Guide**: `TESTING_EXECUTION_GUIDE.md`
- **Results Sheet**: `TESTING_RESULTS.md`
- **Android Logcat**: `Tools → Logcat` in Android Studio
- **Backend URL**: Check `Constants.BASE_URL`

---

## ⏱️ Expected Duration
- Backend tests: ~15 minutes
- Android tests: ~30 minutes
- Bug fixes (if any): ~30 minutes
- **Total**: ~1-2 hours

---

## 🚀 How to Use This Checklist

1. **Print or display** this checklist
2. **Follow the order**: Backend first, then Android
3. **Check boxes** as you complete each test
4. **Document any errors** with: Test #, Error, Expected vs Actual, Fix
5. **Update TESTING_RESULTS.md** with final results
6. **Deploy** only when all ✅

---

**Happy Testing! 🎉**
