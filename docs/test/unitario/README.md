# Tests unitarios

Prueban la logica de `AuthService` sin levantar el contexto de Spring ni conectarse a MySQL.

## Hojas de test

- [Registro](registro.md)
- [Login](login.md)

## Ejecucion

```bash
cd backend
./mvnw -Dtest=AuthServiceRegistrationTest test
./mvnw -Dtest=AuthServiceLoginTest test
```
