# Unitario: login

Clase: [AuthServiceLoginTest.java](../../../backend/src/test/java/com/fsp/secureit/unitario/login/AuthServiceLoginTest.java)

## Cobertura

- Permite login con usuario habilitado y contrasena valida.
- Rechaza usernames inexistentes.
- Rechaza contrasenas incorrectas.
- Rechaza usuarios deshabilitados.

## Dependencias

Usa Mockito para simular `UserRepository` y `PasswordEncoder`. No necesita base de datos.
