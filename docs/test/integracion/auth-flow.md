# Integracion: flujo de autenticacion

Clase: [AuthFlowIntegrationTest.java](../../../backend/src/test/java/com/fsp/secureit/integracion/AuthFlowIntegrationTest.java)

## Cobertura

- Registro y login con BCrypt y persistencia real.
- Logout que invalida la sesion HTTP y bloquea posteriores accesos protegidos.
- Rechazo de usernames duplicados.
- Rechazo de payloads de registro invalidos.
- Bloqueo de `/api/auth/me` sin autenticacion.
- Bloqueo de `/api/admin/dashboard` para usuarios normales.
- Acceso de ADMIN a `/api/admin/dashboard` mediante sesion HTTP.

## Requisitos

- MySQL disponible.
- Base `secureit_test` y usuario configurados en `backend/src/test/resources/application.properties`.
- Migraciones Flyway disponibles en `backend/src/main/resources/db/migration`.
