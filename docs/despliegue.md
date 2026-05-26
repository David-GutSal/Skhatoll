# Manual de despliegue — Skhatoll

## Requisitos

- Docker

---

## 1. Descargar compose

```bash
curl -O https://raw.githubusercontent.com/David-GutSal/Skhatoll/develop/docker-compose.yml
```

## 2. Crear `.env`

```bash
DB_URL=jdbc:mysql://host-aiven:puerto/Skhatoll
DB_USERNAME=avnadmin
DB_PASSWORD=contraseña
JWT_SECRET=clave-segura
JWT_EXPIRATION=8640000
ALLOWED_ORIGIN=http://localhost
```

- `ALLOWED_ORIGIN` → URL donde se accede al frontend

## 3. Iniciar

```bash
docker compose up -d
```

App lista en `http://localhost`

## Comandos útiles

```bash
docker compose ps        # estado
docker compose logs -f   # logs en vivo
docker compose down      # detener
```
