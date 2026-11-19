#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

for variable in DB_PASSWORD DB_ROOT_PASSWORD JWT_SECRET CORS_ALLOWED_ORIGINS; do
	if [[ -z "${!variable:-}" ]]; then
		echo "Variable requerida: ${variable}" >&2
		exit 1
	fi
done

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

chmod 600 .env.aws

docker compose --env-file .env.aws -f compose.ec2.yaml pull
docker compose --env-file .env.aws -f compose.ec2.yaml up --build -d

for attempt in {1..30}; do
	if curl --fail --silent --show-error http://localhost/actuator/health >/dev/null; then
		echo "Backend desplegado y saludable."
		exit 0
	fi
	sleep 2
done

docker compose --env-file .env.aws -f compose.ec2.yaml ps
echo "El backend no respondió correctamente al health check." >&2
exit 1
