# 📚 TESTING DOCUMENTATION INDEX

**Proyecto**: Hyper Reset - Performance Tracking System  
**Fecha Creación**: 2026-06-01  
**Objetivo**: Documentación completa para Testing de Permisos (Backend & Android UI)

---

## 🎯 ¿POR QUÉ ESTA DOCUMENTACIÓN?

Hemos implementado correcciones importantes en el sistema de permisos para Deportistas y Coaches. Esta documentación te ayuda a **validar que todo funciona correctamente** antes de deployar a producción.

### Cambios Validados:
✅ Deportistas solo ven sus propios datos  
✅ Coaches solo ven sus propios deportistas  
✅ Permisos correctos en backend y UI  
✅ SessionManager funciona con roles  
✅ Bottom navigation cambia según rol  

---

## 📁 Documentos Incluidos

### 1. 📋 `QUICK_CHECKLIST.md`
**Tipo**: Referencia rápida  
**Tamaño**: 1 página  
**Tiempo**: 2 minutos para leer

**¿Cuándo usarlo?**
- Cuando necesitas una **visión rápida** de todos los tests
- Para **marcar progreso** mientras testeas
- Como **guía de bolsillo** imprimible

**Contiene**:
```
✅ 9 tests backend
✅ 12 tests Android UI  
✅ Checkboxes para marcar
✅ Estimación de tiempo
✅ Status resumen
```

**👉 EMPIEZA AQUÍ si tienes prisa (< 5 min)**

---

### 2. 📖 `TESTING_EXECUTION_GUIDE.md`
**Tipo**: Guía paso a paso detallada  
**Tamaño**: ~10 páginas  
**Tiempo**: 15-20 minutos para leer

**¿Cuándo usarlo?**
- Cuando necesitas **instrucciones exactas** para cada test
- Para saber **cómo usar Postman**
- Para entender **cómo usar el emulador**
- Para resolver **errores comunes**

**Contiene**:
```
✅ Setup inicial
✅ Pasos exactos para Backend tests (con curl/JSON)
✅ Pasos exactos para Android tests
✅ Screenshots conceptuales
✅ Troubleshooting
✅ Cómo reportar bugs
```

**👉 USA ESTO para ejecutar los tests (15-30 min)**

---

### 3. 📊 `TESTING_RESULTS.md`
**Tipo**: Hoja de resultados / tabla de tracking  
**Tamaño**: ~15 páginas  
**Tiempo**: 30-60 minutos para completar

**¿Cuándo usarlo?**
- Para **documentar los resultados** de tus tests
- Para **cambiar valores** de `???` a resultados reales
- Para **reportar bugs** encontrados
- Para la **conclusión final**

**Contiene**:
```
✅ Tablas de tests backend (9 tests)
✅ Tablas de tests UI Android (12+ tests)
✅ Tests de seguridad (403/401)
✅ Tests de consistencia
✅ Sección de errores
✅ Conclusión ejecutiva
```

**👉 RELLENA ESTO mientras ejecutas los tests (30-60 min)**

---

### 4. ⚙️ `postman_collection.json`
**Tipo**: Colección Postman automatizada  
**Tamaño**: ~20KB  
**Tiempo**: 2-5 minutos para importar

**¿Cuándo usarlo?**
- Cuando quieres **automatizar** tests backend en Postman
- Para **ahorrar tiempo** vs hacer tests manuales
- Para **validación automática** de responses

**Contiene**:
```
✅ 9 requests pre-configurados
✅ Scripts de validación automáticos
✅ Variables de entorno (tokens, IDs)
✅ Auto-guardado de tokens
```

**👉 CÓMO IMPORTAR**:
1. Abrir Postman
2. Click: `File → Import`
3. Seleccionar este archivo
4. Configurar `BASE_URL` y IDs
5. Ejecutar requests (cada uno se valida automáticamente)

**Ventaja**: ⚡ 5 minutos vs 15 minutos manual

---

### 5. 🗺️ `TESTING_PLAN_SUMMARY.md` (Este archivo)
**Tipo**: Resumen ejecutivo  
**Tamaño**: ~8 páginas  
**Tiempo**: 5-10 minutos para leer

**¿Cuándo usarlo?**
- Para **entender el plan completo** de testing
- Para **orientación general**
- Para saber **qué documentos usar**
- Para entender **criterios de éxito**

**Contiene**:
```
✅ Resumen de todos los documentos
✅ Flujo recomendado de testing
✅ Success criteria
✅ Key points
✅ Troubleshooting rápido
✅ Índice de referencia
```

---

## 🚀 FLUJO RECOMENDADO

### Opción A: Rápido (< 2 horas)

```
1. Lee QUICK_CHECKLIST.md (5 min)
   ↓
2. Lee TESTING_PLAN_SUMMARY.md (10 min)
   ↓
3. Importa postman_collection.json en Postman (2 min)
   ↓
4. Ejecuta tests Postman (5-10 min)
   ↓
5. Rellena TESTING_RESULTS.md (Backend section)
   ↓
6. Ejecuta tests Android (45 min)
   ↓
7. Rellena TESTING_RESULTS.md (Android section)
   ↓
8. Revisa conclusión en TESTING_RESULTS.md
```

**Tiempo Total**: 60-90 minutos

---

### Opción B: Detallado (3-4 horas)

```
1. Lee TESTING_PLAN_SUMMARY.md (10 min)
   ↓
2. Lee TESTING_EXECUTION_GUIDE.md (15 min)
   ↓
3. Lee TESTING_RESULTS.md (10 min)
   ↓
4. Ejecuta Backend tests (25 min)
   - Opción A: Manual con cURL (TESTING_EXECUTION_GUIDE.md)
   - Opción B: Postman (postman_collection.json)
   ↓
5. Rellena TESTING_RESULTS.md (Backend section - 10 min)
   ↓
6. Ejecuta Android tests (60-90 min)
   ↓
7. Rellena TESTING_RESULTS.md (Android section - 15 min)
   ↓
8. Documenta errores encontrados (10 min)
   ↓
9. Revisa conclusión
```

**Tiempo Total**: 150-180 minutos

---

## 🎯 USO SEGÚN TU SITUACIÓN

### Soy QA y necesito validar todo correctamente
→ **Opción B (Detallado)** con Postman collection

### Necesito validar rápido antes de deployar
→ **Opción A (Rápido)** con checklist

### Quiero entender el plan antes de empezar
→ Lee: `TESTING_PLAN_SUMMARY.md` → luego decide

### Tengo un error específico y necesito debuguearlo
→ Ve a: `TESTING_EXECUTION_GUIDE.md` → Sección "Troubleshooting"

### Necesito documentar resultados para el cliente
→ Usa: `TESTING_RESULTS.md` → Completa todos los campos

---

## 📍 LOCALIZACIÓN DE DOCUMENTOS

Todos en la **raíz del proyecto**:

```
Proyecto_hiper_reset/
├── QUICK_CHECKLIST.md                 ← Empieza aquí (5 min)
├── TESTING_PLAN_SUMMARY.md            ← Resumen completo (este)
├── TESTING_EXECUTION_GUIDE.md         ← Instrucciones detalladas
├── TESTING_RESULTS.md                 ← Hoja de resultados
├── postman_collection.json            ← Collection Postman
└── ... otros archivos del proyecto
```

---

## ✨ FEATURES DE ESTOS DOCUMENTOS

### ✅ Completo
- Cubre todos los aspectos: Backend, UI, Security, Data consistency

### ✅ Práctico
- Instrucciones paso a paso
- Ejemplos reales de JSON/cURL
- URLs exactas a copiar

### ✅ Automatizable
- Postman collection con validaciones automáticas
- Scripts de testing
- Checklists marcables

### ✅ Flexible
- Usa Postman o cURL (a tu elección)
- Tests manuales o automatizados
- Flujo rápido o detallado

### ✅ Referenciable
- Tablas de búsqueda rápida
- Índices
- Links entre documentos

---

## 📊 TESTS INCLUIDOS

### Backend (9 tests + Security)
```
GET /api/citas ........................... [Deportista]
PUT /api/deportistas/{id} ................. [Edit own]
PUT /api/deportistas/{999} ............... [SECURITY: Should 403]
GET /api/deportistas/coaches ............. [List coaches]
GET /api/test-fisicos .................... [Deportista]
GET /api/deportistas/coach/{id} .......... [Coach]
GET /api/resultados/deportista/{id} ...... [Coach own]
GET /api/resultados/deportista/{999} ..... [SECURITY: Should 403]
GET /api/deportistas ..................... [SECURITY: No token]
```

### Android UI (12+ tests)
```
👤 Deportista Role:
   - Login
   - Session persistence
   - Correct tabs (no Deportistas tab)
   - View profile
   - Edit profile
   - Select coach
   - View citas
   - View pruebas

👨‍💼 Coach Role:
   - Login
   - Correct tabs (has Deportistas)
   - View deportistas
   - View citas
```

---

## 🔐 SEGURIDAD VALIDADA

### Critical Security Checks:
```
🔒 Deportista NO puede editar otros perfiles ........ [403 expected]
🔒 Coach NO puede ver otros deportistas ............ [403 expected]
🔒 Coach NO puede ver resultados ajenos ............ [403 expected]
🔒 Sin token = Unauthorized ........................ [401 expected]
🔒 Rol correcto en SessionManager .................. [DEPORTISTA/COACH]
```

**Si alguno falla**: ⚠️ NO DEPLOYAR - hay security vulnerability

---

## ✅ SUCCESS CRITERIA

**Todos estos deben ser ciertos:**

- [x] Backend: 9/9 tests pasan (200 OK)
- [x] Security: 4/4 security checks fallan correctamente (403/401)
- [x] Android: 12+ UI tests funcionales
- [x] SessionManager: Rol guardado correctamente
- [x] Tabs: Diferentes según rol
- [x] Data: Consistente entre UI y Backend
- [x] Errors: Cero 500 errors
- [x] Docs: TESTING_RESULTS.md completado

**Si alguno falla**: Fijar bug + re-test + repeat

---

## 🛠️ HERRAMIENTAS NECESARIAS

| Herramienta | Descarga | Propósito |
|-------------|----------|----------|
| **Postman** | [postman.com](https://www.postman.com) | Tests Backend |
| **Android Studio** | [developer.android.com](https://developer.android.com) | Emulator |
| **cURL** | Pre-installed en Windows | Tests manuales |
| **VS Code** | [code.visualstudio.com](https://code.visualstudio.com) | Editar markdown |

---

## 🔗 QUICK REFERENCE

### "Necesito..."

| Necesidad | Ir a |
|-----------|------|
| Visión rápida | QUICK_CHECKLIST.md |
| Instrucciones detalladas | TESTING_EXECUTION_GUIDE.md |
| Documentar resultados | TESTING_RESULTS.md |
| Tests automatizados | postman_collection.json |
| Entender el plan | TESTING_PLAN_SUMMARY.md (este) |
| Resolver error | TESTING_EXECUTION_GUIDE.md → Troubleshooting |
| Reportar bug | TESTING_RESULTS.md → Errores encontrados |

---

## 📞 SOPORTE

### Errores Comunes
→ Ver: `TESTING_EXECUTION_GUIDE.md` → Sección "Errores Comunes & Fixes"

### ¿Cómo reporto un bug?
→ Ver: `TESTING_EXECUTION_GUIDE.md` → Sección "Cómo Reportar Bugs"

### ¿Qué hago si un test falla?
→ Ver: `TESTING_PLAN_SUMMARY.md` → Sección "Troubleshooting Rápido"

---

## 🚀 PRÓXIMOS PASOS

1. **Elige tu flujo**:
   - Rápido (< 2 horas) → Lee QUICK_CHECKLIST.md
   - Detallado (3-4 horas) → Lee TESTING_EXECUTION_GUIDE.md

2. **Ejecuta los tests** en orden (backend primero, luego Android)

3. **Documenta resultados** en TESTING_RESULTS.md

4. **Reporte conclusión** en TESTING_RESULTS.md → Conclusión

5. **Si todo OK**: Commit + Push + Ready for production ✅

---

## 📈 PROGRESS TRACKING

Usa este checklist para saber dónde estás:

- [ ] Leído QUICK_CHECKLIST.md
- [ ] Leído TESTING_PLAN_SUMMARY.md
- [ ] Backend tests completados (9/9)
- [ ] Android tests completados (12/12)
- [ ] Resultados documentados en TESTING_RESULTS.md
- [ ] Errores reportados (0 = éxito)
- [ ] Conclusión: READY FOR PRODUCTION
- [ ] Commit + Push completado

---

## 💡 TIPS PRO

1. **Abre 4 ventanas**:
   - Markdown (TESTING_EXECUTION_GUIDE.md)
   - Postman
   - Android Emulator
   - TESTING_RESULTS.md (para anotar)

2. **Mantén Logcat abierto**: Verás errores en tiempo real

3. **Toma screenshots**: De cada paso importante

4. **No saltees pasos**: El orden importa

5. **Haz commits parciales**: Después de Backend, después de Android

---

## 🎓 APRENDIZAJES

### Qué valida este plan:
```
✅ Permisos correctos por rol
✅ Backend retorna datos filtrados
✅ UI muestra datos correctos según rol
✅ Tokens se guardan/se limpian
✅ Datos consistentes UI ↔ Backend
✅ Seguridad: 403/401 cuando corresponde
```

### Qué NO valida:
```
❌ Performance bajo carga
❌ Tests con millones de registros
❌ Múltiples usuarios concurrentes
❌ Offline mode
```

---

## 📅 VERSIONADO

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | 2026-06-01 | Plan inicial |
| TBD | TBD | Actualizaciones después de tests |

---

## 🏁 CONCLUSIÓN

Este plan de testing completo te ayuda a:

✅ **Validar** que todos los permisos funcionan correctamente  
✅ **Documentar** resultados de forma profesional  
✅ **Identificar** bugs antes de producción  
✅ **Deployar** con confianza  

---

## 📝 AUTOFIRMA

Después de completar todos los tests, firma aquí:

```
Testeo completado por: ___________________
Fecha: ________________
Status Final: ✅ READY FOR PRODUCTION / ❌ NEEDS FIXES
```

---

**¿Preguntas?** Consulta el documento específico según tu necesidad.

**¿Listo para empezar?** → Ve a: QUICK_CHECKLIST.md o TESTING_EXECUTION_GUIDE.md

🚀 **¡Adelante con el testing!**
