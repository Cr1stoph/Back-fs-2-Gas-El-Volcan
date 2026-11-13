# Back-fs-2-Gas-El-Volcan
Backend para Gas El Volcan

Requiere JDK 21 para compilar y ejecutar.

## Ejecutar

```powershell
.\mvnw.cmd spring-boot:run
```

El proyecto usa una base H2 persistente en `./data/gas-volcan`. Al iniciar por
primera vez se cargan los seis productos de demostración del frontend.

## Ejecutar con Docker

Requiere Docker Desktop iniciado:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

La API queda disponible en `http://localhost:8080`. Docker levanta MySQL en un
contenedor separado y conserva sus datos en el volumen `db_data`.

Para detener los contenedores:

```powershell
docker compose down
```

## API

La API queda disponible en `http://localhost:8080`.

El estado de salud para AWS y Docker está disponible en
`http://localhost:8080/actuator/health`.

La documentación interactiva Swagger está disponible en
`http://localhost:8080/swagger-ui.html` y el contrato OpenAPI en
`http://localhost:8080/v3/api-docs`. Para probar endpoints protegidos, primero
inicia sesión en `/api/auth/login` y usa el botón **Authorize** con el token
JWT obtenido.

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

## Despliegue del backend en AWS

La configuración está preparada para ejecutar el backend en una instancia EC2
con Docker y conectar la base de datos a Amazon RDS MySQL:

1. Crear una instancia EC2 con Docker instalado y abrir el puerto `8080` en el
   Security Group. Para producción, restringirlo al origen del frontend o usar
   un reverse proxy HTTPS.
2. Crear una instancia RDS MySQL y permitir conexiones desde el Security Group
   de la EC2 en el puerto `3306`.
3. Clonar el repositorio en la EC2, copiar `.env.example` a `.env` y reemplazar
   `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_ROOT_PASSWORD`,
   `JWT_SECRET` y `CORS_ALLOWED_ORIGINS` con valores reales.
4. Ejecutar:

```bash
docker compose up --build -d
docker compose ps
curl http://localhost:8080/actuator/health
```

Para RDS, `DB_HOST` debe ser el endpoint DNS de la instancia. No subir `.env`
al repositorio ni usar las credenciales de ejemplo en AWS.
