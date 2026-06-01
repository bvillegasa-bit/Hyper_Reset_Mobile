# Testing Results - Permisos Backend & UI Android

**Fecha**: 2026-06-01
**Proyecto**: Hyper Reset - Performance Tracking System
**Scope**: Validación de correcciones de permisos para Deportistas y Coaches

---

## 📋 Resumen Ejecutivo

Este documento registra los resultados de testing para verificar que:
1. ✅ Backend APIs retornan 200 OK con permisos correctos
2. ✅ Android UI renderiza correctamente según rol (DEPORTISTA/COACH)
3. ✅ SessionManager mantiene rol y token correctamente
4. ✅ Las transacciones de datos entre UI y Backend funcionan sin errores

---

## 🔧 FASE 1: Backend Testing (Postman/curl)

### Test 1.1: GET /api/citas (como DEPORTISTA)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `GET /api/citas` |
| **Auth** | JWT Token (Deportista) |
| **Expected** | 200 OK + Lista de citas del deportista |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | Verificar que solo retorna citas del deportista, no de otros |

**Curl Command:**
```bash
curl -X GET "https://your-backend.com/api/citas" \
  -H "Authorization: Bearer <DEPORTISTA_TOKEN>" \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "deportista": {"id": 100, "nombre": "Juan Pérez"},
      "coach": {"id": 50, "nombre": "Carlos García"},
      "fecha": "2026-06-05",
      "hora": "10:00",
      "tipo": "Evaluación"
    }
  ],
  "message": "Citas retrieved successfully"
}
```

---

### Test 1.2: GET /api/citas (como COACH)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `GET /api/citas` |
| **Auth** | JWT Token (Coach) |
| **Expected** | 200 OK + Lista de citas como coach |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | Verificar que retorna citas donde el coach es el usuario logueado |

---

### Test 1.3: PUT /api/deportistas/{id} (como DEPORTISTA)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `PUT /api/deportistas/{id}` |
| **Auth** | JWT Token (Deportista) |
| **Method** | PUT |
| **Expected** | 200 OK - Puede editar su propio perfil |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | Un deportista solo debe editar su propio perfil (id == su userId) |

**Curl Command:**
```bash
curl -X PUT "https://your-backend.com/api/deportistas/100" \
  -H "Authorization: Bearer <DEPORTISTA_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.nuevo@email.com",
    "telefono": "+34 600 123 456",
    "direccion": "Calle Nueva 123",
    "coachId": 50
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "id": 100,
    "nombre": "Juan Pérez",
    "email": "juan.nuevo@email.com",
    "telefono": "+34 600 123 456",
    "direccion": "Calle Nueva 123",
    "coachId": 50
  },
  "message": "Deportista updated successfully"
}
```

---

### Test 1.4: PUT /api/deportistas/{id} (DEPORTISTA intenta editar otro)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `PUT /api/deportistas/{OTRO_ID}` |
| **Auth** | JWT Token (Deportista A) |
| **Expected** | 403 Forbidden - No puede editar perfil de otro |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | SECURITY CHECK: Verificar que un deportista NO puede editar perfil de otro deportista |

---

### Test 1.5: GET /api/deportistas/coaches (como DEPORTISTA)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `GET /api/deportistas/coaches` |
| **Auth** | JWT Token (Deportista) |
| **Expected** | 200 OK + Lista de coaches disponibles |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | Endpoint para que deportista seleccione un coach |

**Curl Command:**
```bash
curl -X GET "https://your-backend.com/api/deportistas/coaches" \
  -H "Authorization: Bearer <DEPORTISTA_TOKEN>" \
  -H "Content-Type: application/json"
```

---

### Test 1.6: GET /api/test-fisicos (como DEPORTISTA)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `GET /api/test-fisicos` |
| **Auth** | JWT Token (Deportista) |
| **Expected** | 200 OK + Lista de pruebas físicas del deportista |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | Solo debe retornar pruebas del deportista logueado |

---

### Test 1.7: GET /api/deportistas/{coachId}/coach (como COACH)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `GET /api/deportistas/coach/{coachId}` |
| **Auth** | JWT Token (Coach) |
| **Expected** | 200 OK + Lista de deportistas asignados a este coach |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | Coach solo ve sus propios deportistas |

---

### Test 1.8: GET /api/resultados/deportista/{deportistaId} (como COACH)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `GET /api/resultados/deportista/{deportistaId}` |
| **Auth** | JWT Token (Coach) |
| **Expected** | 200 OK si el deportista está asignado al coach |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | Coach solo accede resultados de sus propios deportistas |

---

### Test 1.9: GET /api/resultados/deportista/{deportistaId} (como OTRO_COACH)

| Campo | Valor |
|-------|-------|
| **Endpoint** | `GET /api/resultados/deportista/{deportistaId}` |
| **Auth** | JWT Token (Otro Coach) |
| **Expected** | 403 Forbidden - No es el coach asignado |
| **Actual** | ??? |
| **Status** | ⏳ Pendiente |
| **Notes** | SECURITY: Coach no puede ver resultados de deportistas de otro coach |

---

## 📱 FASE 2: Android UI Testing (Emulador)

### Test 2.1: Login como Deportista

| Caso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **Login Success** | Ingresar email/password de deportista | ✅ Token saved en SessionManager | ??? | ⏳ |
| **Session Persistence** | Restart app después de login | ✅ SessionManager.isLoggedIn() == true | ??? | ⏳ |
| **Role Detection** | Verificar rol después de login | ✅ SessionManager.getUserRole() == "DEPORTISTA" | ??? | ⏳ |
| **Home Navigation** | Verificar qué pantalla se muestra | ✅ Se muestra HomeActivity con tabs de deportista | ??? | ⏳ |

---

### Test 2.2: Home Activity - Tabs de Deportista

| Elemento | Expected | Actual | Status |
|----------|----------|--------|--------|
| **Bottom Navigation** | Muestra: Inicio, Perfil, Citas, Pruebas (NO Deportistas) | ??? | ⏳ |
| **Inicio Tab** | Muestra datos del deportista logueado | ??? | ⏳ |
| **Perfil Tab** | ✅ Visible y funcional | ??? | ⏳ |
| **Citas Tab** | ✅ Visible y funcional | ??? | ⏳ |
| **Pruebas Tab** | ✅ Visible y funcional | ??? | ⏳ |

---

### Test 2.3: Perfil Tab - Deportista (Ver datos)

| Elemento | Expected | Actual | Status |
|----------|----------|--------|--------|
| **Nombre** | Muestra nombre del deportista | ??? | ⏳ |
| **Email** | Muestra email | ??? | ⏳ |
| **Teléfono** | Muestra teléfono (si existe) | ??? | ⏳ |
| **Dirección** | Muestra dirección (si existe) | ??? | ⏳ |
| **Coach Asignado** | Muestra nombre del coach (si existe) | ??? | ⏳ |
| **Botón Editar** | ✅ Visible para editar datos | ??? | ⏳ |
| **Botón Seleccionar Coach** | ✅ Visible para cambiar coach | ??? | ⏳ |

---

### Test 2.4: Perfil Tab - Editar Perfil

| Paso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **1. Abrir formulario** | Click "Editar Perfil" | ✅ Se abre DeportistaFormFragment | ??? | ⏳ |
| **2. Editar Email** | Cambiar email a nuevo | ✅ Campo editable | ??? | ⏳ |
| **3. Editar Teléfono** | Cambiar teléfono | ✅ Campo editable | ??? | ⏳ |
| **4. Editar Dirección** | Cambiar dirección | ✅ Campo editable | ??? | ⏳ |
| **5. Guardar** | Click "Guardar" | ✅ PUT /api/deportistas/{id} retorna 200 | ??? | ⏳ |
| **6. Confirm UI** | Retorna a Perfil Tab | ✅ Datos actualizados en pantalla | ??? | ⏳ |
| **7. Verify Backend** | Hacer GET /api/auth/profile | ✅ Datos nuevos en respuesta | ??? | ⏳ |

---

### Test 2.5: Perfil Tab - Seleccionar Coach

| Paso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **1. Abrir Selector** | Click "Seleccionar Coach" | ✅ Se abre dropdown con lista de coaches | ??? | ⏳ |
| **2. GET Coaches** | App hace GET /api/deportistas/coaches | ✅ Retorna 200 con lista | ??? | ⏳ |
| **3. Select Coach** | Seleccionar un coach de la lista | ✅ Coach queda seleccionado | ??? | ⏳ |
| **4. Save** | Click "Guardar" | ✅ PUT /api/deportistas/{id} con coachId | ??? | ⏳ |
| **5. Backend Update** | Backend recibe coachId | ✅ Deportista.coachId actualizado en BD | ??? | ⏳ |
| **6. UI Confirm** | Retorna a Perfil Tab | ✅ Muestra nombre del nuevo coach | ??? | ⏳ |

---

### Test 2.6: Citas Tab - Deportista

| Paso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **1. Navigate** | Click en tab "Citas" | ✅ Se muestra CitaListFragment | ??? | ⏳ |
| **2. API Call** | App hace GET /api/citas | ✅ Retorna 200 con citas del deportista | ??? | ⏳ |
| **3. List Display** | Citas se muestran en lista | ✅ Muestra fecha, hora, coach | ??? | ⏳ |
| **4. Only My Citas** | Verificar que solo muestra CITAS del deportista logueado | ✅ No muestra citas de otros deportistas | ??? | ⏳ |

---

### Test 2.7: Pruebas Físicas Tab - Deportista

| Paso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **1. Navigate** | Click en tab "Pruebas" | ✅ Se muestra TestListFragment | ??? | ⏳ |
| **2. API Call** | App hace GET /api/test-fisicos | ✅ Retorna 200 con pruebas del deportista | ??? | ⏳ |
| **3. List Display** | Pruebas se muestran en lista | ✅ Muestra nombre, tipo, fecha | ??? | ⏳ |
| **4. Only My Tests** | Verificar que solo muestra pruebas del deportista | ✅ No muestra pruebas de otros deportistas | ??? | ⏳ |

---

### Test 2.8: Login como Coach

| Caso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **Login Success** | Ingresar email/password de coach | ✅ Token saved | ??? | ⏳ |
| **Role Detection** | SessionManager.getUserRole() | ✅ "COACH" | ??? | ⏳ |
| **Home Navigation** | Verificar qué pantalla se muestra | ✅ HomeActivity con tabs de coach | ??? | ⏳ |

---

### Test 2.9: Home Activity - Tabs de Coach

| Elemento | Expected | Actual | Status |
|----------|----------|--------|--------|
| **Bottom Navigation** | Muestra: Inicio, Deportistas, Citas, Reportes (NO Perfil personal) | ??? | ⏳ |
| **Deportistas Tab** | ✅ Lista de deportistas asignados | ??? | ⏳ |
| **Citas Tab** | ✅ Citas donde el coach es usuario logueado | ??? | ⏳ |
| **Reportes Tab** | ✅ Reportes de deportistas | ??? | ⏳ |

---

### Test 2.10: Deportistas Tab - Coach (Ver lista de pacientes)

| Paso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **1. Navigate** | Click en tab "Deportistas" | ✅ DeportistaListFragment | ??? | ⏳ |
| **2. API Call** | App hace GET /api/deportistas/coach/{coachId} | ✅ 200 OK | ??? | ⏳ |
| **3. List Display** | Se muestra lista de deportistas asignados | ✅ Nombre, email, teléfono | ??? | ⏳ |
| **4. Only My Sportspeople** | Verificar que SOLO muestra deportistas asignados a este coach | ✅ No muestra otros | ??? | ⏳ |
| **5. Edit Option** | Click en un deportista | ✅ Se puede editar datos del deportista | ??? | ⏳ |

---

### Test 2.11: Edit Deportista - Coach

| Paso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **1. Select Sportsman** | Click en un deportista de la lista | ✅ Se abre DeportistaFormFragment | ??? | ⏳ |
| **2. Edit Fields** | Editar email, teléfono, dirección | ✅ Campos editables | ??? | ⏳ |
| **3. Save** | Click "Guardar" | ✅ PUT /api/deportistas/{id} retorna 200 | ??? | ⏳ |
| **4. Backend Update** | Datos se actualizan en BD | ✅ Verificar con GET /api/deportistas/{id} | ??? | ⏳ |
| **5. UI Confirm** | Retorna a lista de deportistas | ✅ Datos actualizados en UI | ??? | ⏳ |

---

### Test 2.12: Citas Tab - Coach

| Paso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **1. Navigate** | Click en tab "Citas" | ✅ CitaListFragment | ??? | ⏳ |
| **2. API Call** | App hace GET /api/citas | ✅ Retorna 200 | ??? | ⏳ |
| **3. Coach's Citas** | Muestra SOLO citas donde el coach es el usuario logueado | ✅ Como coach de esas citas | ??? | ⏳ |

---

## 🔐 FASE 3: Security & Authorization Tests

### Test 3.1: Deportista intenta acceder a endpoint COACH

| Endpoint | Auth (Deportista) | Expected | Actual | Status |
|----------|-------------------|----------|--------|--------|
| `GET /api/deportistas` | ✗ | 403 Forbidden | ??? | ⏳ |
| `GET /api/deportistas/coach/{id}` | ✗ | 403 Forbidden | ??? | ⏳ |

---

### Test 3.2: Token Expiration

| Caso | Acción | Expected | Actual | Status |
|------|--------|----------|--------|--------|
| **Expired Token** | Hacer request con token expirado | ✅ 401 Unauthorized | ??? | ⏳ |
| **App Response** | App debe detectar 401 | ✅ Redirige a LoginActivity | ??? | ⏳ |

---

### Test 3.3: No Token

| Endpoint | Without Token | Expected | Actual | Status |
|----------|---------------|----------|--------|--------|
| `GET /api/deportistas` | ✓ Sin Bearer | 401 Unauthorized | ??? | ⏳ |
| `PUT /api/deportistas/{id}` | ✓ Sin Bearer | 401 Unauthorized | ??? | ⏳ |

---

## 📊 FASE 4: Data Consistency Tests

### Test 4.1: Datos en Perfil vs Backend

| Campo | UI Muestra | Backend Retorna | Match | Status |
|-------|----------|-----------------|-------|--------|
| **Nombre** | ??? | ??? | ✓/✗ | ⏳ |
| **Email** | ??? | ??? | ✓/✗ | ⏳ |
| **Teléfono** | ??? | ??? | ✓/✗ | ⏳ |
| **Coach** | ??? | ??? | ✓/✗ | ⏳ |

---

### Test 4.2: Citas - Filtrado correcto

| Rol | Expected Count | Actual Count | Match | Status |
|-----|-----------------|--------------|-------|--------|
| **Deportista A** | Solo sus citas | ??? | ✓/✗ | ⏳ |
| **Deportista B** | Solo sus citas | ??? | ✓/✗ | ⏳ |
| **Coach A** | Sus citas como coach | ??? | ✓/✗ | ⏳ |

---

### Test 4.3: Resultados - Filtrado correcto

| Rol | Access | Expected | Actual | Status |
|-----|--------|----------|--------|--------|
| **Deportista** | Propios resultados | 200 OK | ??? | ⏳ |
| **Coach** | Resultados de sus deportistas | 200 OK | ??? | ⏳ |
| **Otro Coach** | Resultados de deportista ajeno | 403 Forbidden | ??? | ⏳ |

---

## 🐛 Errores Encontrados

| # | Error | Severidad | Reproducción | Fix Sugerido | Status |
|---|-------|-----------|---------------|------------|--------|
| E1 | ??? | ??? | ??? | ??? | ⏳ |
| E2 | ??? | ??? | ??? | ??? | ⏳ |

---

## ✅ Conclusión

### Status General

- [ ] Todos los tests Backend pasaron
- [ ] Todos los tests UI pasaron
- [ ] Todos los tests de Seguridad pasaron
- [ ] Todos los tests de Consistencia pasaron
- [ ] **Resultado Final: READY FOR PRODUCTION** ✅

### Resumen

**TOTAL TESTS**: 50+
**PASSED**: ???
**FAILED**: ???
**PENDING**: ???

---

## 📝 Notas Técnicas

### Endpoints Base
- **Backend URL**: `https://your-backend.com`
- **Android API Base**: Definido en `Constants.BASE_URL`

### Test Users

#### Deportista
```
Email: deportista@example.com
Password: password123
Expected Role: DEPORTISTA
Expected Permissions: Ver citas, ver pruebas, editar perfil, seleccionar coach
```

#### Coach
```
Email: coach@example.com
Password: password123
Expected Role: COACH
Expected Permissions: Ver deportistas, editar deportistas, ver citas, ver reportes
```

### Herramientas Recomendadas

1. **Postman** - Para tests de Backend API
2. **Android Emulator** - Para tests de UI
3. **Android Studio Logcat** - Para debugging de logs
4. **cURL** - Para tests rápidos de endpoints

---

## 🔗 Referencias

- [Android SessionManager](app/src/main/java/com/hyperreset/app/utils/SessionManager.java)
- [RetrofitClient](app/src/main/java/com/hyperreset/app/data/api/RetrofitClient.java)
- [ApiService](app/src/main/java/com/hyperreset/app/data/api/ApiService.java)
- [HomeActivity](app/src/main/java/com/hyperreset/app/ui/home/HomeActivity.java)
- [DeportistaFormFragment](app/src/main/java/com/hyperreset/app/ui/deportistas/form/DeportistaFormFragment.java)

---

**Última actualización**: 2026-06-01
**Responsable del testing**: [Tu nombre]
**Status de Deployment**: ⏳ En progreso
