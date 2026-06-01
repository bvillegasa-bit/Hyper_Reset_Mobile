# 📋 Testing Plan Summary - Hyper Reset Permissions

**Fecha**: 2026-06-01  
**Objetivo**: Validar correcciones de permisos para Deportistas y Coaches  
**Status**: Ready to Execute

---

## 📁 Documentos Creados

### 1️⃣ `TESTING_RESULTS.md` (Hoja de Resultados Principal)
**Propósito**: Documento maestro para registrar todos los resultados de testing

**Contiene**:
- Tablas detalladas de tests backend (9 tests)
- Tablas detalladas de tests UI Android (12+ tests)
- Tests de seguridad (SECURITY CHECKS)
- Tests de consistencia de datos
- Sección para errores encontrados
- Resumen final y conclusión

**Cómo usar**:
- Abrir en VS Code o editor de markdown
- Ejecutar cada test en orden
- Cambiar `???` por resultados reales
- Cambiar `⏳` por `✅` o `❌` según resultado

---

### 2️⃣ `TESTING_EXECUTION_GUIDE.md` (Guía Paso a Paso)
**Propósito**: Instrucciones detalladas para ejecutar cada test

**Contiene**:
- Instrucciones de setup inicial
- Pasos exactos para cada test backend (con URLs y headers)
- Pasos exactos para cada test Android
- Cómo obtener tokens de prueba
- Cómo usar Postman
- Cómo usar Android Emulator
- Resolución de errores comunes
- Cómo reportar bugs

**Cómo usar**:
- Seguir la guía secuencialmente
- Copiar comandos curl o URLs a Postman
- Marcar cada paso completado

---

### 3️⃣ `postman_collection.json` (Colección Postman Automática)
**Propósito**: Automatizar tests backend en Postman

**Contiene**:
- Requests pre-configurados para todos los tests
- Scripts de validación automáticos (tests)
- Variables de entorno (tokens, IDs)
- Auto-guardado de tokens después de login

**Cómo usar**:
1. Abrir Postman
2. Click: `File → Import`
3. Seleccionar `postman_collection.json`
4. Configurar variables: `BASE_URL`, user IDs
5. Ejecutar cada request
6. Verificar resultados en "Test Results" tab

**Ventajas**:
- ✅ Tests se ejecutan automáticamente
- ✅ Valida responses automáticamente
- ✅ Tokens se guardan entre requests
- ✅ Tiempo ~5 minutos vs 15 minutos manual

---

### 4️⃣ `QUICK_CHECKLIST.md` (Checklist de Bolsillo)
**Propósito**: Referencia rápida con todos los tests en una página

**Contiene**:
- Todos los tests backend (9)
- Todos los tests Android (12)
- Checkboxes para marcar completados
- Resumen final
- Estimación de tiempo

**Cómo usar**:
- Imprimir o mostrar mientras testeas
- Marcar checkboxes conforme avanzas
- Verificación final rápida

---

## 🧪 Tests Incluidos

### Fase 1: Backend Tests (9 tests + Security)
```
✅ GET /api/citas (Deportista)
✅ PUT /api/deportistas/{id} (Editar propio perfil)
🔒 PUT /api/deportistas/{999} (Security - Editar otro - debe fallar)
✅ GET /api/deportistas/coaches
✅ GET /api/test-fisicos (Deportista)
✅ GET /api/deportistas/coach/{id} (Coach)
✅ GET /api/resultados/deportista/{id} (Coach - own)
🔒 GET /api/resultados/deportista/{999} (Security - Other - debe fallar)
🔒 GET /api/deportistas (No token - debe fallar)
```

### Fase 2: Android UI Tests (12+ tests)
```
👤 Deportista:
  ✅ Login
  ✅ SessionManager role verification
  ✅ Bottom nav tabs (correcto para rol)
  ✅ Ver perfil
  ✅ Editar perfil + sync backend
  ✅ Seleccionar coach
  ✅ Ver citas
  ✅ Ver pruebas
  ✅ Logout

👨‍💼 Coach:
  ✅ Login
  ✅ Bottom nav tabs (diferentes)
  ✅ Ver deportistas asignados
  ✅ Ver citas
```

### Fase 3: Data Consistency & Security
```
🔒 Verificar permisos correctos por rol
📊 Datos en UI = Backend
🔐 Tokens se guardan/se limpian correctamente
```

---

## 🚀 Flujo de Testing Recomendado

### Día 1 - Backend (30-45 minutos)
```
1. Setup: Obtener tokens (5 min)
2. Backend tests manual o Postman (25 min)
3. Documentar resultados (5 min)
```

### Día 2 - Android UI (45-60 minutos)
```
1. Setup Emulator (10 min)
2. Android tests (40 min)
3. Documentar resultados (5 min)
```

### Día 3 - Bug Fixes (si hay errores)
```
1. Analizar errores
2. Implementar fixes
3. Re-test
```

---

## 📊 Success Criteria

### ✅ Todo debe pasar:
- [x] Backend: 9 tests + 4 security checks
- [x] Android: 12+ UI tests  
- [x] No 403/401 errores excepto en security tests esperados
- [x] SessionManager funciona correctamente
- [x] Datos consistentes UI ↔ Backend
- [x] Tokens persistidos correctamente

### 🚀 Deployment Requirements:
```
✅ Todos los tests PASS
✅ Cero bugs críticos
✅ TESTING_RESULTS.md actualizado
✅ Código limpio y sin warnings
✅ Documentación actualizada
→ READY FOR PRODUCTION
```

---

## 📝 Key Files Modified/Created

```
NUEVO:
├── TESTING_RESULTS.md              ← Hoja de resultados principal
├── TESTING_EXECUTION_GUIDE.md      ← Guía paso a paso
├── postman_collection.json         ← Tests Postman automatizados
└── QUICK_CHECKLIST.md              ← Checklist de bolsillo

EXISTENTES (sin cambios para testing):
├── app/src/main/java/com/hyperreset/app/utils/SessionManager.java
├── app/src/main/java/com/hyperreset/app/data/api/RetrofitClient.java
├── app/src/main/java/com/hyperreset/app/ui/home/HomeActivity.java
└── app/src/main/java/com/hyperreset/app/data/api/ApiService.java
```

---

## 🛠️ Herramientas Necesarias

| Herramienta | Versión | Propósito |
|------------|---------|----------|
| **Postman** | Latest | Tests Backend |
| **Android Studio** | 2022+ | Android Emulator |
| **Android Emulator** | API 31+ | Testing UI |
| **cURL** (opcional) | Any | Tests manuales |
| **Browser** | Any | Visualizar markdown |

---

## ⚠️ Puntos Críticos de Seguridad

Estos tests **DEBEN** fallar con 403/401:

```
1. ❌ Deportista edita perfil de otro deportista
2. ❌ Coach accede deportistas de otro coach  
3. ❌ Coach accede resultados de otro coach
4. ❌ Cualquier endpoint sin token (401)
```

Si alguno de estos **PASA** (retorna 200), **NO DEPLOYAR** - hay vulnerability.

---

## 🐛 Troubleshooting Rápido

### Backend Tests fallan
```
→ Verificar BASE_URL correcta
→ Verificar tokens válidos y no expirados
→ Verificar IDs de deportistas/coaches existen
→ Revisar backend logs para errores
```

### Android Tests fallan
```
→ Emulator correctamente iniciado
→ API Base URL correcta en Constants.java
→ Token visible en Logcat (no null)
→ Verificar layout resources existen
```

### No sincroniza datos UI ↔ Backend
```
→ Verificar POST/PUT/GET se hacen con token
→ Revisar Logcat para errores HTTP
→ Verificar ResponseCode != 200
→ Verificar JSON parsing en modelos
```

---

## 📞 Próximos Pasos

1. **Ejecutar Backend Tests** → TESTING_EXECUTION_GUIDE.md
2. **Ejecutar Android Tests** → TESTING_EXECUTION_GUIDE.md  
3. **Documentar Resultados** → TESTING_RESULTS.md
4. **Si hay errores** → Hacer fixes y re-test
5. **Si todo OK** → Actualizar TESTING_RESULTS.md con conclusión ✅
6. **Commit & Push** → `git add . && git commit -m "Testing: All tests passed" && git push`
7. **Release** → Ready for production

---

## 📋 Archivo de Referencia Rápida

**¿Dónde encontrar qué?**

| Pregunta | Respuesta |
|----------|----------|
| "¿Por dónde empiezo?" | → QUICK_CHECKLIST.md |
| "¿Cómo hago el test 1.2?" | → TESTING_EXECUTION_GUIDE.md #1.2 |
| "¿Dónde anoto resultados?" | → TESTING_RESULTS.md |
| "¿Cómo hago tests en Postman?" | → postman_collection.json |
| "¿Dónde está el error?" | → TESTING_RESULTS.md #Errores |
| "¿Qué debo hacer antes de deployar?" | → TESTING_RESULTS.md #Conclusión |

---

## ✨ Tips Profesionales

1. **Usa Postman Collection** - Más rápido que curl
2. **Mantén Logcat abierto** - Verás errores en tiempo real
3. **Toma screenshots** - Para documentar bugs
4. **Ejecuta tests en orden** - No saltes pasos
5. **Anota los problemas** - Con detalles exactos
6. **Re-test después del fix** - Nunca asumas que funciona
7. **Commit después de cada fase** - No pierdes trabajo

---

**🎯 Objetivo Final**: Validar que todos los permisos funcionan correctamente ANTES de deployment a producción.

**💡 Recuerda**: Un test que falla ahora es mejor que un bug en producción.

---

**Fecha de Creación**: 2026-06-01  
**Status**: READY TO EXECUTE  
**Próximo Paso**: Comenzar con QUICK_CHECKLIST.md

🚀 **¡Buena suerte!**
