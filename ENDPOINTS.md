# Café Aroma API - Documentación de Endpoints

API REST correspondiente al proyecto **Sistema Web para la Gestión de Ventas Online - Café Aroma**.

## URL base

```text
http://localhost:8080
```

## Autenticación

Los endpoints protegidos utilizan autenticación mediante JWT.

El token debe enviarse en el encabezado HTTP:

Authorization: Bearer <TOKEN>
Roles
ADMIN: administración de categorías, productos y pedidos.
CLIENTE: perfil de cliente, direcciones, carrito, pedidos y pagos.
1. Autenticación y perfil
Comprobar funcionamiento del servicio

Método: GET

Endpoint:

/api/auth/prueba

Acceso: Público.

Respuesta esperada:

{
  "mensaje": "Servicio web Café Aroma funcionando correctamente."
}
Registrar cliente

Método: POST

Endpoint:

/api/auth/registro

Acceso: Público.

Entrada:

{
  "nombre": "Usuario Prueba",
  "correo": "usuario@cafearoma.com",
  "password": "123456",
  "telefono": "3001234567"
}

Salida: información básica del usuario registrado y token JWT.

Respuesta satisfactoria: 201 Created.

Iniciar sesión

Método: POST

Endpoint:

/api/auth/login

Acceso: Público.

Entrada:

{
  "correo": "usuario@cafearoma.com",
  "password": "123456"
}

Salida: mensaje de autenticación, token JWT e información del usuario.

Respuesta satisfactoria: 200 OK.

Credenciales incorrectas: 401 Unauthorized.

Consultar perfil

Método: GET

Endpoint:

/api/auth/perfil

Acceso: Usuario autenticado.

Salida:

{
  "idUsuario": 3,
  "nombre": "Daniel Naranjo",
  "correo": "daniel@cafearoma.com",
  "rol": "CLIENTE",
  "telefono": "3001234567"
}
Actualizar perfil

Método: PUT

Endpoint:

/api/auth/perfil

Acceso: Usuario autenticado.

Entrada:

{
  "nombre": "Daniel Naranjo Actualizado",
  "telefono": "3112223344"
}
Cambiar contraseña

Método: PUT

Endpoint:

/api/auth/cambiar-password

Acceso: Usuario autenticado.

Entrada:

{
  "passwordActual": "123456",
  "passwordNueva": "Nueva123"
}

Salida satisfactoria:

{
  "mensaje": "Contraseña actualizada correctamente."
}
2. Categorías
Listar categorías activas

Método: GET

/api/categorias

Acceso: Público.

Crear categoría

Método: POST

/api/admin/categorias

Acceso: ADMIN.

Entrada:

{
  "nombreCategoria": "Café en grano",
  "descripcion": "Productos de café en presentación de grano"
}

Respuesta satisfactoria: 201 Created.

Actualizar categoría

Método: PUT

/api/admin/categorias/{idCategoria}

Acceso: ADMIN.

Entrada:

{
  "nombreCategoria": "Café Premium",
  "descripcion": "Categoría actualizada"
}
Desactivar categoría

Método: DELETE

/api/admin/categorias/{idCategoria}

Acceso: ADMIN.

La eliminación es lógica: la categoría queda inactiva.

3. Productos
Listar productos activos

Método: GET

/api/productos

Acceso: Público.

Consultar producto por ID

Método: GET

/api/productos/{idProducto}

Acceso: Público.

Listar productos por categoría

Método: GET

/api/productos/categoria/{idCategoria}

Acceso: Público.

Buscar productos por nombre

Método: GET

/api/productos/buscar?nombre=cafe

Acceso: Público.

Crear producto

Método: POST

/api/admin/productos

Acceso: ADMIN.

Entrada:

{
  "nombreProducto": "Café Premium",
  "descripcion": "Café colombiano de alta calidad",
  "precio": 45000,
  "stock": 20,
  "imagenUrl": "https://ejemplo.com/cafe.jpg",
  "idCategoria": 1
}

Respuesta satisfactoria: 201 Created.

Actualizar producto

Método: PUT

/api/admin/productos/{idProducto}

Acceso: ADMIN.

Utiliza la misma estructura de entrada utilizada para crear el producto.

Desactivar producto

Método: DELETE

/api/admin/productos/{idProducto}

Acceso: ADMIN.

La eliminación es lógica: el producto queda inactivo.

4. Carrito de compras

Todos los endpoints de este módulo requieren autenticación con rol CLIENTE.

Consultar carrito

Método: GET

/api/carrito

Salida: identificador del carrito, estado, productos y valor total.

Agregar producto al carrito

Método: POST

/api/carrito/items

Entrada:

{
  "idProducto": 4,
  "cantidad": 2
}

El sistema valida que el producto esté activo y que exista stock suficiente.

Actualizar cantidad

Método: PUT

/api/carrito/items/{idItem}

Entrada:

{
  "cantidad": 3
}
Eliminar un producto del carrito

Método: DELETE

/api/carrito/items/{idItem}
Vaciar carrito

Método: DELETE

/api/carrito
5. Direcciones de envío

Todos los endpoints requieren autenticación con rol CLIENTE.

Listar mis direcciones

Método: GET

/api/direcciones
Crear dirección

Método: POST

/api/direcciones

Entrada:

{
  "direccion": "Calle 123 # 45-67",
  "ciudad": "Bogotá",
  "departamento": "Cundinamarca",
  "pais": "Colombia",
  "codigoPostal": "110111",
  "referencia": "Apto 302",
  "predeterminada": true
}

La primera dirección registrada para el cliente se establece automáticamente como predeterminada.

Respuesta satisfactoria: 201 Created.

Actualizar dirección

Método: PUT

/api/direcciones/{idDireccion}

Utiliza la misma estructura de entrada del registro de dirección.

Desactivar dirección

Método: DELETE

/api/direcciones/{idDireccion}

La dirección se desactiva en lugar de eliminarse físicamente.

Marcar dirección como predeterminada

Método: PUT

/api/direcciones/{idDireccion}/predeterminada

No requiere cuerpo de solicitud.

6. Pedidos
Crear pedido desde el carrito

Método: POST

/api/pedidos

Acceso: CLIENTE.

Entrada opcional:

{
  "idDireccionEnvio": 1
}

Si no se proporciona una dirección, el sistema utiliza la dirección predeterminada del cliente.

El pedido se crea utilizando los productos existentes en el carrito activo.

El sistema:

valida el carrito;
valida productos y existencias;
calcula el total;
crea el pedido;
crea los detalles;
descuenta el stock;
cierra el carrito.

Respuesta satisfactoria: 201 Created.

Listar mis pedidos

Método: GET

/api/pedidos/mis-pedidos

Acceso: CLIENTE.

Consultar uno de mis pedidos

Método: GET

/api/pedidos/{idPedido}

Acceso: CLIENTE.

El sistema verifica que el pedido pertenezca al usuario autenticado.

Listar todos los pedidos

Método: GET

/api/admin/pedidos

Acceso: ADMIN.

Cambiar estado de un pedido

Método: PUT

/api/admin/pedidos/{idPedido}/estado

Acceso: ADMIN.

Entrada:

{
  "estado": "EN_PREPARACION"
}

Estados admitidos:

PENDIENTE
PAGADO
EN_PREPARACION
ENVIADO
ENTREGADO
CANCELADO
7. Pagos
Simular pago

Método: POST

/api/pagos/simular

Acceso: CLIENTE.

Entrada:

{
  "idPedido": 1,
  "metodoPago": "TARJETA",
  "aprobado": true
}

Salida:

{
  "idPago": 1,
  "idPedido": 1,
  "metodoPago": "TARJETA",
  "valorPagado": 90000,
  "estadoPago": "APROBADO",
  "fechaPago": "..."
}

El servicio implementa una simulación académica del proceso de pago.

Si el pago es aprobado:

Pago -> APROBADO
Pedido -> PAGADO

Si el pago es rechazado:

Pago -> RECHAZADO
Pedido -> CANCELADO
Stock -> restaurado
Códigos HTTP utilizados
Código	Significado
200 OK	Solicitud procesada correctamente
201 Created	Recurso creado correctamente
400 Bad Request	Error de validación o regla de negocio
401 Unauthorized	Credenciales o autenticación inválidas
403 Forbidden	El usuario no posee permisos para acceder al recurso
Seguridad de los endpoints
Grupo	Acceso
Registro y login	Público
Consulta de categorías	Público
Consulta de productos	Público
Perfil	Usuario autenticado
Carrito	CLIENTE
Direcciones	CLIENTE
Pedidos de cliente	CLIENTE
Pagos	CLIENTE
/api/admin/**	ADMIN
Tecnologías relacionadas

La API utiliza:

Java
Spring Boot
Spring MVC
Spring Data JPA
Spring Security
JWT
BCrypt
MariaDB
Maven
JUnit
Mockito
Postman