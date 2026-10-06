# Tests de integracion

Prueban el flujo real con Spring Boot, Spring Security, Flyway, BCrypt, sesiones HTTP y MySQL.

## Hoja de test

- [Flujo de autenticacion](auth-flow.md)

## Ejecucion

```bash
cd backend
./mvnw -Dtest=AuthFlowIntegrationTest test
```

## Base de datos

Los tests usan `secureit_test`. Antes de cada test se ejecutan:

```text
Flyway clean
Flyway migrate
```

La base de desarrollo `secureit` no se modifica.
