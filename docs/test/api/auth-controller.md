# API: AuthController

Clase: [AuthControllerApiTest.java](../../../backend/src/test/java/com/fsp/secureit/api/AuthControllerApiTest.java)

## Endpoints cubiertos

### `POST /api/auth/register`

- Registro correcto con respuesta `201 Created`.
- Payload invalido con respuesta `400 Bad Request`.
- Username duplicado con respuesta `409 Conflict`.

### `POST /api/auth/login`

- Login correcto con respuesta `200 OK`.
- Comprueba mensaje, username y autoridad `ROLE_USER`.

## Alcance

La suite usa mocks para `AuthService` y `AuthenticationManager`. La validacion Bean Validation y `GlobalExceptionHandler` se prueban dentro de una configuracion standalone de `MockMvc`.
