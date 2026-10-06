# Unitario: registro

Clase: [AuthServiceRegistrationTest.java](../../../backend/src/test/java/com/fsp/secureit/unitario/registro/AuthServiceRegistrationTest.java)

## Cobertura

- Crea usuarios habilitados con rol `USER`.
- Codifica la contrasena antes de guardar el usuario.
- Rechaza usernames duplicados.
- Rechaza emails duplicados.

## Dependencias

Usa Mockito para simular `UserRepository` y `PasswordEncoder`. No necesita base de datos.
