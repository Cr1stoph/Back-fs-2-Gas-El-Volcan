#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if [[ -n "${DB_PASSWORD:-}" && -n "${DB_ROOT_PASSWORD:-}" && -n "${JWT_SECRET:-}" && -n "${CORS_ALLOWED_ORIGINS:-}" ]]; then
	cat > .env.aws <<EOF
DB_NAME=gasvolcan
DB_USER=gasvolcan
DB_PASSWORD=${DB_PASSWORD}
DB_ROOT_PASSWORD=${DB_ROOT_PASSWORD}
DB_HOST=db
JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}
EOF
fi

if [[ ! -s .env.aws ]]; then
	echo "No se encontró un archivo .env.aws válido." >&2
	exit 1
fi

for variable in DB_PASSWORD DB_ROOT_PASSWORD JWT_SECRET CORS_ALLOWED_ORIGINS; do
	if ! grep -q "^${variable}=.\+" .env.aws; then
		echo "Variable requerida ausente o vacía en .env.aws: ${variable}" >&2
		exit 1
	fi
done

chmod 600 .env.aws

# Liberar espacio antes de reconstruir. NO se usan --volumes para preservar la BD.
echo "Espacio antes de la limpieza:"
df -h / || true
docker container prune -f || true
docker image prune -af || true
docker builder prune -af || true
echo "Espacio después de la limpieza:"
df -h / || true

docker compose --env-file .env.aws -f compose.ec2.yaml pull
docker compose --env-file .env.aws -f compose.ec2.yaml up --build -d

for attempt in {1..240}; do
	if curl --fail --silent --show-error http://localhost/actuator/health >/dev/null; then
		echo "Backend desplegado y saludable."
		exit 0
	fi
	sleep 2
done

docker compose --env-file .env.aws -f compose.ec2.yaml ps
echo "El backend no respondió correctamente al health check." >&2
exit 1
