# CafeAromaAPI

API REST para el proyecto **Sistema Web para la Gestión de Ventas Online - Café Aroma**.

El backend está desarrollado con **Java, Spring Boot y MariaDB** y permite gestionar las principales operaciones de una tienda virtual de productos de café, incluyendo autenticación, catálogo, carrito de compras, direcciones de envío, pedidos y simulación de pagos.

---

## Estado del proyecto

La API cuenta actualmente con los siguientes módulos funcionales:

- Autenticación y registro de usuarios.
- Inicio de sesión mediante JWT.
- Roles `ADMIN` y `CLIENTE`.
- Contraseñas cifradas mediante BCrypt.
- Consulta y actualización del perfil.
- Cambio de contraseña.
- Gestión de categorías.
- Gestión de productos.
- Catálogo público.
- Carrito de compras.
- Gestión de direcciones de envío.
- Creación y consulta de pedidos.
- Administración del estado de los pedidos.
- Simulación de pagos.
- Control y actualización de stock.
- Protección de endpoints según rol.
- Pruebas unitarias automatizadas.

---

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT
- BCrypt
- MariaDB
- Maven
- Maven Wrapper
- JUnit 5
- Mockito
- IntelliJ IDEA
- Postman
- Git
- GitHub

---

## Arquitectura

El proyecto aplica una arquitectura organizada por capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MariaDB
```

Adicionalmente se utilizan:

```text
DTO       → transferencia de datos entre cliente y API
Model     → representación de las entidades persistentes
Config    → seguridad, JWT y configuración del sistema
```

Esta separación permite mantener responsabilidades independientes y facilita el mantenimiento, reutilización y pruebas de los componentes.

---

## Estructura general

```text
src/
├── main/
│   ├── java/com/cafearoma/api/
│   │   │
│   │   ├── config/
│   │   │   ├── DataInitializer.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── SecurityConfig.java
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── CarritoController.java
│   │   │   ├── CategoriaController.java
│   │   │   ├── DireccionEnvioController.java
│   │   │   ├── PagoController.java
│   │   │   ├── PedidoController.java
│   │   │   └── ProductoController.java
│   │   │
│   │   ├── dto/
│   │   │   ├── ActualizarCantidadRequest.java
│   │   │   ├── ActualizarPerfilRequest.java
│   │   │   ├── AgregarCarritoRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── CambiarPasswordRequest.java
│   │   │   ├── CarritoItemResponse.java
│   │   │   ├── CarritoResponse.java
│   │   │   ├── CategoriaRequest.java
│   │   │   ├── CategoriaResponse.java
│   │   │   ├── CrearPedidoRequest.java
│   │   │   ├── DireccionEnvioRequest.java
│   │   │   ├── DireccionEnvioResponse.java
│   │   │   ├── EstadoPedidoRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── PagoRequest.java
│   │   │   ├── PagoResponse.java
│   │   │   ├── PedidoItemResponse.java
│   │   │   ├── PedidoResponse.java
│   │   │   ├── PerfilResponse.java
│   │   │   ├── ProductoRequest.java
│   │   │   ├── ProductoResponse.java
│   │   │   ├── RegistroRequest.java
│   │   │   └── UsuarioResponse.java
│   │   │
│   │   ├── model/
│   │   │   ├── Carrito.java
│   │   │   ├── CarritoItem.java
│   │   │   ├── Categoria.java
│   │   │   ├── Cliente.java
│   │   │   ├── DetallePedido.java
│   │   │   ├── DireccionEnvio.java
│   │   │   ├── Pago.java
│   │   │   ├── Pedido.java
│   │   │   ├── Producto.java
│   │   │   ├── Rol.java
│   │   │   └── Usuario.java
│   │   │
│   │   ├── repository/
│   │   │   ├── CarritoItemRepository.java
│   │   │   ├── CarritoRepository.java
│   │   │   ├── CategoriaRepository.java
│   │   │   ├── ClienteRepository.java
│   │   │   ├── DetallePedidoRepository.java
│   │   │   ├── DireccionEnvioRepository.java
│   │   │   ├── PagoRepository.java
│   │   │   ├── PedidoRepository.java
│   │   │   ├── ProductoRepository.java
│   │   │   ├── RolRepository.java
│   │   │   └── UsuarioRepository.java
│   │   │
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── CarritoService.java
│   │   │   ├── CategoriaService.java
│   │   │   ├── DireccionEnvioService.java
│   │   │   ├── JwtService.java
│   │   │   ├── PagoService.java
│   │   │   ├── PedidoService.java
│   │   │   ├── PerfilService.java
│   │   │   ├── ProductoService.java
│   │   │   └── UsuarioDetailsService.java
│   │   │
│   │   └── CafeAromaApiApplication.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/com/cafearoma/api/
        ├── CafeAromaApiApplicationTests.java
        └── service/
            ├── AuthServiceTest.java
            ├── CarritoServiceTest.java
            ├── CategoriaServiceTest.java
            ├── DireccionEnvioServiceTest.java
            ├── PagoServiceTest.java
            ├── PedidoServiceTest.java
            ├── PerfilServiceTest.java
            └── ProductoServiceTest.java
```

---

## Módulos principales

### Autenticación y perfil

Permite registrar clientes, iniciar sesión, obtener el perfil, actualizar datos personales y cambiar la contraseña.

La autenticación se realiza mediante **JSON Web Token (JWT)**.

### Categorías

Permite consultar las categorías activas y realizar operaciones administrativas de creación, actualización y desactivación.

### Productos

Permite consultar el catálogo, buscar productos, filtrar por categoría y realizar operaciones administrativas sobre los productos.

### Carrito de compras

Permite al cliente:

- visualizar su carrito;
- agregar productos;
- actualizar cantidades;
- eliminar productos;
- vaciar el carrito.

El sistema valida disponibilidad y stock antes de agregar o modificar productos.

### Direcciones de envío

Permite al cliente registrar, actualizar, consultar y desactivar direcciones de envío, así como seleccionar una dirección predeterminada.

### Pedidos

Los pedidos se generan utilizando el contenido del carrito.

Durante la creación del pedido el sistema:

1. valida el cliente y la dirección;
2. obtiene el carrito activo;
3. verifica la disponibilidad y stock;
4. calcula el total;
5. crea el pedido;
6. almacena sus detalles;
7. descuenta las unidades del inventario;
8. cierra el carrito.

### Pagos

El proyecto implementa una **simulación académica de pagos**.

Cuando un pago es aprobado:

```text
Pago   → APROBADO
Pedido → PAGADO
```

Cuando es rechazado:

```text
Pago   → RECHAZADO
Pedido → CANCELADO
Stock  → RESTAURADO
```

---

## Seguridad

La API utiliza **Spring Security, JWT y BCrypt**.

La autorización está organizada de la siguiente manera:

| Recurso | Acceso |
|---|---|
| Registro y login | Público |
| Consulta de categorías | Público |
| Consulta de productos | Público |
| Perfil | Usuario autenticado |
| Carrito | `CLIENTE` |
| Direcciones | `CLIENTE` |
| Pedidos del cliente | `CLIENTE` |
| Pagos | `CLIENTE` |
| `/api/admin/**` | `ADMIN` |

El JWT debe enviarse en los endpoints protegidos mediante:

```text
Authorization: Bearer <TOKEN>
```

---

## Base de datos

El proyecto utiliza **MariaDB**.

Nombre de la base de datos utilizada durante el desarrollo:

```text
cafe_aroma
```

La conexión se configura mediante `application.properties`.

Ejemplo:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/cafe_aroma
spring.datasource.username=<USUARIO>
spring.datasource.password=<PASSWORD>
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
```

> Las credenciales y claves sensibles no deben publicarse en el repositorio.

---

## Ejecución del proyecto

### Requisitos

- Java 17.
- MariaDB.
- Base de datos `cafe_aroma` configurada.
- Maven Wrapper incluido en el proyecto.

En Windows puede ejecutarse con:

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda disponible por defecto en:

```text
http://localhost:8080
```

Para comprobar su funcionamiento:

```text
GET http://localhost:8080/api/auth/prueba
```

Respuesta esperada:

```json
{
  "mensaje": "Servicio web Café Aroma funcionando correctamente."
}
```

---

## Pruebas unitarias

El proyecto incluye pruebas automatizadas desarrolladas con **JUnit 5 y Mockito** para los principales servicios:

```text
AuthService
CarritoService
CategoriaService
DireccionEnvioService
PagoService
PedidoService
PerfilService
ProductoService
```

Las pruebas pueden ejecutarse en Windows mediante:

```powershell
.\mvnw.cmd test
```

Resultado obtenido:

```text
Tests run: 21
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Se prueban tanto escenarios satisfactorios como reglas de negocio y casos de error.

---

## Generación del ejecutable

Para compilar, ejecutar las pruebas y generar el archivo `.jar`:

```powershell
.\mvnw.cmd clean package
```

El archivo generado se encontrará en:

```text
target/
```

y tendrá un nombre correspondiente a la versión configurada en Maven, por ejemplo:

```text
CafeAromaAPI-0.0.1-SNAPSHOT.jar
```

---

## Documentación de la API

La documentación completa de métodos HTTP, endpoints, entradas, salidas, permisos y ejemplos se encuentra en:

```text
ENDPOINTS.md
```

---

## Control de versiones

El proyecto utiliza **Git** para el control de versiones y **GitHub** como repositorio remoto.

El código fuente y su historial de cambios se mantienen versionados durante el desarrollo.

---

## Estado de las pruebas

La suite actual fue ejecutada de manera conjunta mediante Maven Wrapper con el siguiente resultado:

```text
Tests run: 21, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

Por lo tanto, las pruebas automatizadas implementadas se encuentran aprobadas satisfactoriamente.

---

## Proyecto académico

**Café Aroma** forma parte del proyecto formativo de desarrollo de software y tiene como propósito demostrar la construcción e integración de los componentes de una aplicación orientada a la web.