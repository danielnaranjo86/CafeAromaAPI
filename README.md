# CafeAromaAPI

API REST para el proyecto **Sistema Web para la Gestión de Ventas Online - Café Aroma**.

Este backend está desarrollado con **Java + Spring Boot + MariaDB** y tiene como objetivo servir como base para una tienda online de productos de café. La API será consumida posteriormente por un frontend web en React y, más adelante, por una aplicación móvil.

---

## Estado actual del proyecto

Actualmente la API cuenta con los siguientes módulos funcionales:

- Autenticación de usuarios.
- Registro de clientes.
- Inicio de sesión con JWT.
- Roles `ADMIN` y `CLIENTE`.
- Contraseñas cifradas con BCrypt.
- Perfil autenticado.
- Gestión de categorías.
- Gestión de productos.
- Catálogo público.
- Protección de rutas administrativas.

---

## Tecnologías utilizadas

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- BCrypt
- MariaDB
- Maven
- IntelliJ IDEA
- Postman

---

## Estructura general del proyecto

```text
src/main/java/com/cafearoma/api
├── config
│   ├── DataInitializer.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── controller
│   ├── AuthController.java
│   ├── CategoriaController.java
│   └── ProductoController.java
├── dto
│   ├── AuthResponse.java
│   ├── CategoriaRequest.java
│   ├── CategoriaResponse.java
│   ├── LoginRequest.java
│   ├── ProductoRequest.java
│   ├── ProductoResponse.java
│   ├── RegistroRequest.java
│   └── UsuarioResponse.java
├── model
│   ├── Categoria.java
│   ├── Cliente.java
│   ├── Producto.java
│   ├── Rol.java
│   └── Usuario.java
├── repository
│   ├── CategoriaRepository.java
│   ├── ClienteRepository.java
│   ├── ProductoRepository.java
│   ├── RolRepository.java
│   └── UsuarioRepository.java
├── service
│   ├── AuthService.java
│   ├── CategoriaService.java
│   ├── JwtService.java
│   ├── ProductoService.java
│   └── UsuarioDetailsService.java
└── CafeAromaApiApplication.java