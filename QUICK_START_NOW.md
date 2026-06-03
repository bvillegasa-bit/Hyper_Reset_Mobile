# ⚡ QUICK START - QUÉ HACER AHORA

## 🎯 ESTADO ACTUAL
```
✅ Backend: Código compilado, permisos arreglados
✅ Android: APK compilada, role-based UI implementada
✅ Testing: 7 documentos + Postman collection listo
✅ Archivos: Todo en repo, lista para deploy/test
```

---

## 🚀 PRÓXIMOS 5 PASOS

### PASO 1️⃣: RESTART BACKEND (5 minutos)
Backend necesita reiniciarse con código actualizado.

**Opción A: Si está en XAMPP/Local**
```bash
cd C:\Users\BernabeA.LAPTOP-KB1N2IHM\AndroidStudioProjects\hyper-reset-backend
mvn clean package -DskipTests
# Detener proceso anterior
java -jar target/hyper-reset-api-*.jar --spring.profiles.active=dev
```

**Opción B: Si está en Ngrok (Current)**
```bash
# Backend ya está corriendo en Ngrok
# Solo necesita restart si lo hiciste en local
# Ngrok URL actual: https://erythemal-subadditively-nichole.ngrok-free.dev/api/
```

✅ **Validar**: Abre en navegador:
```
https://erythemal-subadditively-nichole.ngrok-free.dev/api/health
# Debe retornar 200 OK con status "UP"
```

---

### PASO 2️⃣: INSTALAR ANDROID APK (5 minutos)

APK está lista en:
```
C:\Users\BernabeA.LAPTOP-KB1N2IHM\AndroidStudioProjects\Proyecto_hiper_reset\
app\build\outputs\apk\debug\app-debug.apk
```

**En Emulador:**
```bash
adb install -r app-debug.apk
# O drag-and-drop en Android Studio
```

**En Device Real (USB):**
```bash
adb devices
# Verificar que aparece tu device
adb install -r app-debug.apk
```

---

### PASO 3️⃣: LOGIN COMO DEPORTISTA (2 minutos)

**Usuario de prueba:**
```
Email: carlos.mendoza@email.com
Password: Temporal123! (o tu password temporal si creaste nuevo)
```

**Verificar después de login:**
- ✅ Tabs muestran: Inicio → Pruebas → Citas → Mensajes → Perfil
- ✅ (Sin "Deportistas" que es solo para Coach)
- ✅ SessionManager guarda rol = "DEPORTISTA"

---

### PASO 4️⃣: QUICK TEST (10 minutos)

**Test 1: Ver Perfil**
- Tap en "Perfil" tab
- Verifica: Nombre, email, teléfono, dirección
- Verifica: Botón "Editar Perfil" existe

**Test 2: Editar Perfil**
- Click "Editar Perfil"
- Cambiar: Teléfono o dirección
- Click "Guardar"
- Verifica: Datos se actualizan (sin error 403)

**Test 3: Ver Citas**
- Tap en "Citas" tab
- Verifica: Aparecen las citas (sin error 403)
- Verifica: Solo sus citas, no todas

**Test 4: Ver Pruebas**
- Tap en "Pruebas" tab
- Verifica: Aparecen pruebas disponibles

**RESULTADO ESPERADO:** ✅ Sin errores 403
```
❌ Antes: 403 Forbidden
✅ Después: 200 OK con datos
```

---

### PASO 5️⃣: TESTING FORMAL (30-60 minutos)

Cuando todo funcione en quick test, ejecuta:

**Opción A: RÁPIDA (30 min)**
```
1. Lee: TESTING_START_HERE.md
2. Elige: "Quick Testing Path"
3. Sigue: QUICK_CHECKLIST.md
4. Documenta: Resultados en TESTING_RESULTS.md
```

**Opción B: DETALLADA (60 min + Postman)**
```
1. Lee: TESTING_EXECUTION_GUIDE.md
2. Setup: postman_collection.json en Postman
3. Ejecuta: Todos los requests
4. Documenta: Resultados en TESTING_RESULTS.md
```

**Opción C: SOLO POSTMAN (20 min)**
```
1. Importa: postman_collection.json en Postman
2. Click: "Run Collection"
3. Automático testa todos los endpoints
4. Resultados claros: ✅ Pass / ❌ Fail
```

---

## 📋 CHECKLIST RÁPIDO

```
Before Starting:
☐ Backend está running y healthcheck retorna 200 OK
☐ APK está instalado en emulador/device
☐ Tienes el email/password del deportista de prueba

Quick Test (15 min):
☐ Login exitoso como Deportista
☐ Tabs correctos (5 items, sin "Deportistas")
☐ Perfil muestra datos sin 403
☐ Editar perfil guarda sin 403
☐ Ver citas sin 403
☐ Ver pruebas sin 403

Formal Testing (30-60 min):
☐ Completar QUICK_CHECKLIST.md
☐ O ejecutar postman_collection.json
☐ Documentar en TESTING_RESULTS.md
☐ Todos tests ✅ Pass

Ready to Deploy:
☐ Backend restarted ✅
☐ Quick test completado ✅
☐ Formal test completado ✅
☐ Todos endpoints 200 OK ✅
☐ Sin errores 403/401 inesperados ✅
```

---

## 🆘 TROUBLESHOOTING RÁPIDO

### Problema: "Still getting 403 on /api/citas"
**Solución:**
1. Verifica backend fue restarted con código nuevo
2. Ejecuta: `curl https://erythemal-subadditively-nichole.ngrok-free.dev/api/citas -H "Authorization: Bearer YOUR_TOKEN"`
3. Si 403, backend no fue actualizado. Reinicia.

### Problema: "APK crashes on login"
**Solución:**
1. Verifica token se guarda en SessionManager
2. Revisa: `adb logcat | grep SessionManager`
3. Si error, verifica AuthResponse.java tiene deportistaId

### Problema: "Tabs no cambian según rol"
**Solución:**
1. Verifica: `SessionManager.isDeportista()` retorna true
2. Revisa HomeActivity.java línea donde se setup Bottom Nav
3. Si problema, recompila con `./gradlew clean assembleDebug`

### Problema: "Postman collection no funciona"
**Solución:**
1. Verifica token en postman_collection.json es válido
2. Obtén nuevo token: `POST /api/auth/login` con credenciales
3. Actualiza token en Postman collection pre-request script

---

## 📞 DOCUMENTOS DISPONIBLES

| Documento | Leer cuando | Tiempo |
|-----------|------------|--------|
| **FAST_TRACK_COMPLETADO.md** | Ahora | 10 min |
| **TESTING_START_HERE.md** | Antes de testing | 5 min |
| **QUICK_CHECKLIST.md** | Durante quick test | 10 min |
| **TESTING_EXECUTION_GUIDE.md** | Formal testing | 30 min |
| **TESTING_RESULTS.md** | Documentar resultados | 60 min |
| **postman_collection.json** | Testing backend | 20 min |

---

## ⏰ TIMELINE ESTIMADO

```
NOW:
├─ 5 min ............. Restart backend
├─ 5 min ............. Instalar APK
├─ 2 min ............. Login como Deportista
├─ 10 min ............ Quick test
└─ 30-60 min ......... Formal testing
                     ━━━━━━━━━━━
                     ≈ 52-82 minutos TOTAL
```

---

## 🎉 WHEN YOU'RE DONE

Si todos tests pasan ✅:
1. Commit code: `git add . && git commit -m "Fast Track: Deportista permisos arreglados"`
2. Push: `git push origin main`
3. Next: Otros features (Planes, Reportes PDF, FCM notifications)

Si algo falla ❌:
1. Consulta troubleshooting arriba
2. Revisa logs: `adb logcat` (Android) o `tail -f app.log` (Backend)
3. Si persiste, usa documentos detallados (TESTING_EXECUTION_GUIDE.md)

---

**Good luck! 🚀**

*Implementación Fast Track completada el 1 de junio de 2026*
