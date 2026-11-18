#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
	echo "Uso: $0 dominio correo" >&2
	exit 1
fi

DOMINIO="$1"
CORREO="$2"

docker run --rm \
	-p 80:80 \
	-v certbot_conf:/etc/letsencrypt \
	-v certbot_www:/var/www/certbot \
	certbot/certbot certonly \
	--standalone \
	--non-interactive \
	--agree-tos \
	--preferred-challenges http \
	-m "$CORREO" \
	-d "$DOMINIO"

echo "Certificado obtenido para $DOMINIO."
