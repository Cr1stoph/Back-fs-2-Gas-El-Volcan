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
inicia sesión en `/api/v1/auth/login` y usa el botón **Authorize** con el token
JWT obtenido.

| Método | Endpoint | Uso |
|---|---|---|
| GET | `/api/v1/productos` | Listar catálogo |
| GET | `/api/v1/productos/{id}` | Ver un producto |
| POST | `/api/v1/productos` | Crear producto |
| PUT | `/api/v1/productos/{id}` | Editar producto |
| DELETE | `/api/v1/productos/{id}` | Eliminar producto |
| GET | `/api/v1/pedidos` | Listar pedidos |
| GET | `/api/v1/pedidos/{id}` | Ver comprobante |
| POST | `/api/v1/pedidos` | Crear pedido y descontar stock |
| PATCH | `/api/v1/pedidos/{id}/estado` | Avanzar pedido |

Las rutas de autenticación están bajo `/api/v1/auth`:

| Método | Endpoint | Uso |
|---|---|---|
| POST | `/api/v1/auth/register` | Registrar un cliente |
| POST | `/api/v1/auth/login` | Iniciar sesión y obtener JWT |
| POST | `/api/v1/auth/refresh` | Renovar un JWT vigente |

El sistema administra los roles `ROLE_ADMIN`, `ROLE_VENDEDOR` y
`ROLE_CLIENTE`. La demo inicial crea usuarios con las contraseñas indicadas
para pruebas académicas y las credenciales deben cambiarse antes de producción.

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

## Despliegue en AWS Learner Lab

El laboratorio puede restringir RDS y otros servicios administrados. Para la
evaluación existen dos opciones de base de datos:

- **MySQL dentro de EC2:** usar `compose.ec2.yaml`. Esta opción no depende de
  RDS y conserva los datos en el volumen Docker `db_data`.
- **Amazon RDS:** usar `compose.rds.yaml` si tu Learner Lab permite crear RDS
  y configurar su Security Group.

Antes de desplegar, copia `.env.aws.example` a `.env.aws`, reemplaza todas las
contraseñas y configura `CORS_ALLOWED_ORIGINS` con el origen HTTPS de GitHub
Pages.

Instala Docker en Ubuntu EC2 o Amazon Linux 2023:

```bash
chmod +x scripts/configurar-ec2.sh
./scripts/configurar-ec2.sh
exit
```

Después vuelve a conectarte y, para MySQL dentro de EC2:

```bash
cp .env.aws.example .env.aws
docker compose --env-file .env.aws -f compose.ec2.yaml up --build -d
docker compose --env-file .env.aws -f compose.ec2.yaml ps
curl http://localhost/actuator/health
```

Para RDS:

```bash
DB_HOST=endpoint-rds \
docker compose --env-file .env.aws -f compose.rds.yaml up --build -d
```

No uses la opción RDS si el laboratorio no permite crear instancias RDS. AWS
Academy suele reiniciar las credenciales y los recursos al cerrar el Lab, por
lo que documenta la IP pública y las evidencias antes de cerrar la sesión.
