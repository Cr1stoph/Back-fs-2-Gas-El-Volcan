#!/usr/bin/env bash
set -euo pipefail

if command -v apt-get >/dev/null 2>&1; then
	sudo apt-get update
	sudo apt-get install -y ca-certificates curl docker.io docker-compose-v2
	sudo systemctl enable docker
	sudo systemctl start docker
	sudo usermod -aG docker "$USER"
elif command -v dnf >/dev/null 2>&1; then
	sudo dnf update -y
	sudo dnf install -y docker
	sudo systemctl enable docker
	sudo systemctl start docker
	sudo usermod -aG docker ec2-user
else
	echo "Distribución no compatible con este script." >&2
	exit 1
fi

echo "Docker instalado."
echo "Cierra la sesión SSH y vuelve a conectarte para usar Docker sin sudo."
