# Back-fs-2-Gas-El-Volcan
Backend para Gas El Volcan

Requiere JDK 25 (LTS) para compilar y ejecutar.

## Ejecutar

```powershell
.\mvnw.cmd spring-boot:run
```

El proyecto usa una base H2 persistente en `./data/gas-volcan`. Al iniciar por
primera vez se cargan los seis productos de demostración del frontend.

## API

La API queda disponible en `http://localhost:8080`.

| Método | Endpoint | Uso |
|---|---|---|
| GET | `/api/productos` | Listar catálogo |
| GET | `/api/productos/{id}` | Ver un producto |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Editar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| GET | `/api/pedidos` | Listar pedidos |
| GET | `/api/pedidos/{id}` | Ver comprobante |
| POST | `/api/pedidos` | Crear pedido y descontar stock |
| PATCH | `/api/pedidos/{id}/estado` | Avanzar pedido |

El checkout conserva las reglas del frontend: despacho de `$2.500`, retiro sin
costo, ofertas solo si son menores al precio normal y tarjeta de prueba de
`0000000000000000` para simular rechazo.
