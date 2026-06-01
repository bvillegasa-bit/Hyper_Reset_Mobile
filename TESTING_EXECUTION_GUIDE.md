# Testing Execution Guide - Hyper Reset

Este guía te ayuda a ejecutar todos los tests de manera sistemática.

## Preparación Inicial

### 1. Obtener Tokens de Test

Primero, necesitas tokens válidos de test. Ejecuta estos logins en Postman:

#### Login Deportista
```
POST https://your-backend.com/api/auth/login
Content-Type: application/json

{
  "email": "deportista@example.com",
  "password": "password123"
}
```

Guarda el `token` de la respuesta como: `DEPORTISTA_TOKEN`

#### Login Coach
```
POST https://your-backend.com/api/auth/login
Content-Type: application/json

{
  "email": "coach@example.com",
  "password": "password123"
}
```

Guarda el `token` como: `COACH_TOKEN`

---

## FASE 1: Testing Backend

### 1.1 Test: GET /api/citas (Deportista)

En Postman:
1. Crear nuevo request
2. Método: `GET`
3. URL: `https://your-backend.com/api/citas`
4. Headers:
   - `Authorization: Bearer <DEPORTISTA_TOKEN>`
   - `Content-Type: application/json`
5. Click Send
6. ✅ Verify: Status 200 OK
7. ✅ Verify: Response contiene array de citas
8. **📝 Resultado**: `______________` (200 OK ✅ / 403 ❌ / Error ❌)

---

### 1.2 Test: PUT /api/deportistas/{id} (Editar perfil - Deportista)

En Postman:
1. Crear nuevo request
2. Método: `PUT`
3. URL: `https://your-backend.com/api/deportistas/100` (reemplaza 100 con ID del deportista)
4. Headers:
   - `Authorization: Bearer <DEPORTISTA_TOKEN>`
   - `Content-Type: application/json`
5. Body (raw JSON):
```json
{
  "email": "deportista.new@email.com",
  "telefono": "+34 600 999 888",
  "direccion": "Calle Nueva 456",
  "coachId": 50
}
```
6. Click Send
7. ✅ Verify: Status 200 OK
8. ✅ Verify: Email en respuesta es el nuevo email
9. **📝 Resultado**: `______________` (200 OK ✅ / 403 ❌ / Error ❌)

---

### 1.3 Test: Security - Deportista edita otro perfil

En Postman:
1. Método: `PUT`
2. URL: `https://your-backend.com/api/deportistas/999` (ID de OTRO deportista)
3. Headers:
   - `Authorization: Bearer <DEPORTISTA_TOKEN>`
4. Body: cualquier cambio
5. Click Send
6. ✅ Verify: Status DEBE ser 403 Forbidden (SECURITY CHECK!)
7. **📝 Resultado**: `______________` (403 ✅ / 200 ❌ / Error ❌)

---

### 1.4 Test: GET /api/deportistas/coaches (Obtener lista de coaches)

En Postman:
1. Método: `GET`
2. URL: `https://your-backend.com/api/deportistas/coaches`
3. Headers:
   - `Authorization: Bearer <DEPORTISTA_TOKEN>`
4. Click Send
5. ✅ Verify: Status 200 OK
6. ✅ Verify: Response contiene array de coaches
7. **📝 Resultado**: `______________` (200 OK ✅ / Error ❌)

---

### 1.5 Test: GET /api/test-fisicos (Deportista)

En Postman:
1. Método: `GET`
2. URL: `https://your-backend.com/api/test-fisicos`
3. Headers:
   - `Authorization: Bearer <DEPORTISTA_TOKEN>`
4. Click Send
5. ✅ Verify: Status 200 OK
6. ✅ Verify: Solo retorna pruebas del deportista logueado
7. **📝 Resultado**: `______________` (200 OK ✅ / Error ❌)

---

### 1.6 Test: GET /api/deportistas/coach/{coachId} (Coach)

En Postman:
1. Método: `GET`
2. URL: `https://your-backend.com/api/deportistas/coach/50` (reemplaza 50 con ID del coach)
3. Headers:
   - `Authorization: Bearer <COACH_TOKEN>`
4. Click Send
5. ✅ Verify: Status 200 OK
6. ✅ Verify: Solo retorna deportistas asignados a este coach
7. **📝 Resultado**: `______________` (200 OK ✅ / Error ❌)

---

### 1.7 Test: GET /api/resultados/deportista/{id} (Coach accede resultados de SU deportista)

En Postman:
1. Método: `GET`
2. URL: `https://your-backend.com/api/resultados/deportista/100` (ID de deportista asignado)
3. Headers:
   - `Authorization: Bearer <COACH_TOKEN>`
4. Click Send
5. ✅ Verify: Status 200 OK
6. **📝 Resultado**: `______________` (200 OK ✅ / Error ❌)

---

### 1.8 Test: Security - Coach accede resultados de deportista AJENO

En Postman:
1. Método: `GET`
2. URL: `https://your-backend.com/api/resultados/deportista/999` (ID de deportista de OTRO coach)
3. Headers:
   - `Authorization: Bearer <COACH_TOKEN>`
4. Click Send
5. ✅ Verify: Status DEBE ser 403 Forbidden (SECURITY!)
6. **📝 Resultado**: `______________` (403 ✅ / 200 ❌ / Error ❌)

---

### 1.9 Test: Sin Token

En Postman:
1. Método: `GET`
2. URL: `https://your-backend.com/api/deportistas`
3. Headers: (SIN Authorization header)
4. Click Send
5. ✅ Verify: Status 401 Unauthorized
6. **📝 Resultado**: `______________` (401 ✅ / 200 ❌ / Error ❌)

---

## FASE 2: Testing Android UI

### Prerequisito: Setup Emulator
```
1. Android Studio → Device Manager
2. Crear emulador (Pixel 4a, API 31+)
3. Start emulator
4. Esperar a que boot complete
5. Abrir proyecto en Android Studio
6. Run app en emulator (Shift+F10)
```

---

### 2.1 Test: Login como Deportista

En Android:
1. ✅ App abre en LoginActivity
2. Ingresar email: `deportista@example.com`
3. Ingresar password: `password123`
4. Click "Iniciar Sesión"
5. ✅ Verify: Navega a HomeActivity
6. ✅ Verify: Bottom navigation muestra tabs DEPORTISTA (no tabs COACH)
7. **📝 Resultado**: Login exitoso ✅ / Error ❌

---

### 2.2 Test: SessionManager - Verificar datos guardados

En Android Studio Console:
1. Abrir Logcat
2. Ejecutar comando:
```
adb shell "am instrument -e class com.hyperreset.app.utils.SessionManager -v 'com.hyperreset.app' 2>&1 | grep -i 'isDeportista\|role\|token'"
```

O en código (agregar en SplashActivity temporalmente):
```java
SessionManager sm = new SessionManager(this);
Log.d("TEST", "Role: " + sm.getUserRole()); // Debe ser: DEPORTISTA
Log.d("TEST", "isLoggedIn: " + sm.isLoggedIn()); // Debe ser: true
```

3. ✅ Verify: getUserRole() retorna "DEPORTISTA"
4. ✅ Verify: isLoggedIn() retorna true
5. **📝 Resultado**: SessionManager correcto ✅ / Error ❌

---

### 2.3 Test: Bottom Navigation - Tabs de Deportista

En Android:
1. En HomeActivity, verificar que muestra:
   - [ ] Tab 1: Inicio (o Home Dashboard)
   - [ ] Tab 2: Perfil
   - [ ] Tab 3: Citas
   - [ ] Tab 4: Pruebas
   - [ ] ❌ NO debe estar: Tab Deportistas

2. Click en cada tab
3. ✅ Verify: Cada tab carga su contenido
4. **📝 Resultado**: Tabs correctos ✅ / Tabs incorrectos ❌

---

### 2.4 Test: Perfil Tab - Ver datos

En Android:
1. Hacer login como deportista (si no lo hiciste)
2. Click en tab "Perfil"
3. ✅ Verify: Se muestra nombre del deportista
4. ✅ Verify: Se muestra email
5. ✅ Verify: Se muestra teléfono (si existe)
6. ✅ Verify: Se muestra dirección (si existe)
7. ✅ Verify: Se muestra Coach asignado (si existe)
8. ✅ Verify: Existe botón "Editar Perfil"
9. ✅ Verify: Existe botón "Seleccionar Coach"
10. **📝 Resultado**: Todo se muestra ✅ / Falta algo ❌

---

### 2.5 Test: Editar Perfil - Cambiar datos

En Android:
1. En Perfil tab, click "Editar Perfil"
2. ✅ Se abre formulario de edición
3. Cambiar:
   - Email: `new.email@test.com`
   - Teléfono: `+34 600 777 999`
   - Dirección: `Nueva Calle 123`
4. Click "Guardar"
5. ✅ Verify: Se muestra loading/progress
6. ✅ Verify: API hace PUT /api/deportistas/{id}
7. ✅ Verify: Retorna a Perfil tab
8. ✅ Verify: Datos actualizados en pantalla
9. ✅ Verify: En Logcat NO hay errores HTTP
10. **📝 Resultado**: Edición exitosa ✅ / Error ❌

---

### 2.6 Test: Seleccionar Coach

En Android:
1. En Perfil tab, click "Seleccionar Coach"
2. ✅ Se abre diálogo o dropdown con lista de coaches
3. ✅ Verify: Lista contiene 2+ coaches
4. Seleccionar un coach de la lista
5. Click "Guardar" (o similar)
6. ✅ Verify: API hace PUT /api/deportistas/{id} con coachId
7. ✅ Verify: Retorna a Perfil
8. ✅ Verify: Nombre del nuevo coach se muestra
9. **📝 Resultado**: Coach seleccionado ✅ / Error ❌

---

### 2.7 Test: Citas Tab - Ver lista de citas

En Android:
1. Click en tab "Citas"
2. ✅ Se muestra lista de citas
3. ✅ Verify: Cada cita muestra: fecha, hora, coach
4. ✅ Verify: Solo muestra CITAS DEL DEPORTISTA LOGUEADO
5. ✅ Verify: No hay errores en Logcat
6. **📝 Resultado**: Citas cargadas ✅ / Error ❌

---

### 2.8 Test: Pruebas Físicas Tab

En Android:
1. Click en tab "Pruebas"
2. ✅ Se muestra lista de pruebas
3. ✅ Verify: Cada prueba muestra: nombre, tipo, fecha
4. ✅ Verify: Solo pruebas del deportista logueado
5. ✅ Verify: No hay errores
6. **📝 Resultado**: Pruebas cargadas ✅ / Error ❌

---

### 2.9 Test: Logout

En Android:
1. En Perfil tab, buscar botón "Cerrar Sesión"
2. Click en logout
3. ✅ Verify: Navega a LoginActivity
4. ✅ Verify: SessionManager.isLoggedIn() == false
5. ✅ Verify: Token se limpia
6. **📝 Resultado**: Logout exitoso ✅ / Error ❌

---

### 2.10 Test: Login como Coach

En Android:
1. En LoginActivity, ingresar:
   - Email: `coach@example.com`
   - Password: `password123`
2. Click "Iniciar Sesión"
3. ✅ Verify: Navega a HomeActivity
4. ✅ Verify: getUserRole() == "COACH"
5. ✅ Verify: Bottom navigation muestra OTROS TABS
   - [ ] Debe tener: Inicio, Deportistas, Citas, Reportes
   - [ ] NO debe tener: Pruebas, edición de perfil personal
6. **📝 Resultado**: Coach login correcto ✅ / Error ❌

---

### 2.11 Test: Deportistas Tab - Coach

En Android (logueado como Coach):
1. Click en tab "Deportistas"
2. ✅ Se muestra lista de deportistas asignados a este coach
3. ✅ Verify: Cada fila muestra: nombre, email, teléfono
4. ✅ Verify: Click en un deportista permite editarlo
5. ✅ Verify: Los datos editados se guardan con PUT
6. **📝 Resultado**: Deportistas tab funciona ✅ / Error ❌

---

### 2.12 Test: Citas Tab - Coach

En Android (logueado como Coach):
1. Click en tab "Citas"
2. ✅ Se muestra lista de citas donde el coach es participante
3. ✅ Verify: Solo citas como coach
4. **📝 Resultado**: Citas coach ✅ / Error ❌

---

## FASE 3: Resumen de Resultados

### Backend Tests
```
1.1 GET /api/citas (Deportista)          : ____ (✅/❌)
1.2 PUT /api/deportistas/{id}            : ____ (✅/❌)
1.3 Security - Edit other profile        : ____ (✅/❌)
1.4 GET /api/deportistas/coaches         : ____ (✅/❌)
1.5 GET /api/test-fisicos                : ____ (✅/❌)
1.6 GET /api/deportistas/coach/{id}      : ____ (✅/❌)
1.7 GET /api/resultados (own)            : ____ (✅/❌)
1.8 Security - Access other results      : ____ (✅/❌)
1.9 No Token request                     : ____ (✅/❌)
```

### Android UI Tests
```
2.1 Login como Deportista                : ____ (✅/❌)
2.2 SessionManager verification          : ____ (✅/❌)
2.3 Bottom Navigation                    : ____ (✅/❌)
2.4 Ver Perfil                           : ____ (✅/❌)
2.5 Editar Perfil                        : ____ (✅/❌)
2.6 Seleccionar Coach                    : ____ (✅/❌)
2.7 Ver Citas                            : ____ (✅/❌)
2.8 Ver Pruebas                          : ____ (✅/❌)
2.9 Logout                               : ____ (✅/❌)
2.10 Login como Coach                    : ____ (✅/❌)
2.11 Deportistas Tab (Coach)             : ____ (✅/❌)
2.12 Citas Tab (Coach)                   : ____ (✅/❌)
```

---

## Errores Comunes & Fixes

### Error: 401 Unauthorized
**Causa**: Token expirado o inválido
**Fix**: Repetir login para obtener nuevo token

### Error: 403 Forbidden
**Causa**: Usuario no tiene permiso
**Fix**: Verificar que endpoint requiere ese rol

### Error: 404 Not Found
**Causa**: Recurso no existe (ID incorrecto)
**Fix**: Verificar que ID existe en BD

### Error en Android: "Unable to resolve host"
**Causa**: Backend URL incorrecta o red
**Fix**: Verificar BASE_URL en Constants.java

### Error en Android: "Token is null"
**Causa**: SessionManager no guardó token
**Fix**: Verificar que login guardó AuthResponse correctamente

---

## Cómo Reportar Bugs

Cuando encuentres un error:
1. Anotarlo aquí con número (E1, E2, etc.)
2. Incluir: método, URL, esperado vs actual
3. Adjuntar screenshot o JSON response
4. Marcar severidad: CRITICAL / HIGH / MEDIUM / LOW

Ejemplo:
```
E1 - CRITICAL
Endpoint: PUT /api/deportistas/{id}
Expected: 200 OK, deportista actualizado
Actual: 400 Bad Request, "email already exists"
Causa: Backend no maneja emails duplicados
Fix: Agregar validación única de email
```

---

## Notas Finales

- ✅ Todos los tests deben pasar ANTES de release
- ✅ Si algo falla, documentarlo y hacer fix
- ✅ Después del fix, repetir el test para confirmar
- ✅ Una vez todo ✅, actualizar TESTING_RESULTS.md
- ✅ Luego hacer commit + push + release

---

**Buena suerte! 🚀**
