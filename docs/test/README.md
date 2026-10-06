# Tests

Indice principal de las pruebas del backend.

## Como funciona

La cobertura esta separada por responsabilidad:

- **Unitarios**: prueban `AuthService` de forma aislada con Mockito. No levantan Spring ni necesitan una base de datos.
- **API**: prueban `AuthController` con `MockMvc` standalone, validacion de payloads y respuestas HTTP. No necesitan una base de datos.
- **Integracion**: levantan el contexto completo de Spring Boot, Spring Security, Flyway y MySQL. Usan la base `secureit_test`.

Los tests de integracion ejecutan `Flyway.clean()` y `Flyway.migrate()` antes de cada test. Esto evita que los datos de una prueba afecten a otra.

## Ejecucion

Desde `backend/`:

```bash
./mvnw test
```

Ejecutar una rama concreta:

```bash
./mvnw -Dtest=AuthServiceRegistrationTest test
./mvnw -Dtest=AuthServiceLoginTest test
./mvnw -Dtest=AuthControllerApiTest test
./mvnw -Dtest=AuthFlowIntegrationTest test
```

La configuracion de integracion esta en `backend/src/test/resources/application.properties` y apunta a `secureit_test`, separada de la base de desarrollo `secureit`.

## Indice de ramas

- [Tests unitarios](unitario/README.md)
- [Tests API](api/README.md)
- [Tests de integracion](integracion/README.md)
