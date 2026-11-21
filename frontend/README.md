# Frontend compilado (Vite)

Esta carpeta es el destino del `dist/` que genera el repositorio **fs-2-Gas-El-Volcan**:

```powershell
npm ci
$env:VITE_API_URL = '/api'
$env:VITE_BASE_PATH = '/'
npm run build
scp -i ..\gasvolcan-key.pem -r .\dist\* ubuntu@3.87.119.65:/opt/gas-volcan/frontend/
```

El contenedor `nginx` la monta en `/var/www/gas-volcan` y sirve:

- `/` → SPA React (con fallback a `index.html`).
- `/api/` → `backend:8080` (Spring Boot).
- `/actuator/` y `/swagger-ui/` → `backend:8080`.

No se versiona el contenido del build: solo este archivo mantiene la carpeta en el repositorio.
