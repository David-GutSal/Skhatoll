<div align="center">

# 🎓 TFG – Skhatoll

Aplicación web desarrollada como **Trabajo de Fin de Grado**, basada en una arquitectura
cliente-servidor con frontend SPA y backend desacoplado mediante API REST y WebSocket.

</div>

---

## 🧠 Descripción del proyecto

Skhatoll es una aplicación web desarrollada como proyecto académico, cuyo objetivo es
aplicar de forma práctica los conocimientos adquiridos durante el ciclo formativo de
**Desarrollo de Aplicaciones Web (DAW)**.

El proyecto sigue una **arquitectura MVC desacoplada**, separando claramente el frontend
y el backend, facilitando la escalabilidad, el mantenimiento y el trabajo en equipo.

---

## 🛠️ Tecnologías

<p align="center">
  <img src="https://skillicons.dev/icons?i=html,css,js,vue,bootstrap,java,spring,hibernate,mysql,docker" />  
</p>

### Frontend
- HTML, CSS, JavaScript
- Vue.js
- Vue Router
- Vuex
- Bootstrap

### Backend
- Java
- Spring Boot
- Hibernate / JPA
- Spring WebSocket
- API REST
- MySQL


### DevOps / Herramientas
- Docker & Docker Compose
- Git & GitHub

---

## 📁 Estructura del repositorio

```text
Skhatoll/
├── backend/        # API REST y WebSocket (Spring Boot)
├── frontend/       # Aplicación SPA (Vue.js)
├── database/       # Scripts y configuración de base de datos
├── docker/         # Configuración Docker
├── docs/           # Documentación técnica
├── docker-compose.yml
└── README.md
```
## 🔐 Reglas del repositorio

### 🌿 Reglas de uso de ramas

#### Rama `main`
- Contiene únicamente versiones **estables y funcionales** del proyecto.
- No se permite realizar desarrollo directo sobre esta rama.
- Solo se actualiza mediante Pull Requests desde `develop`.

#### Rama `develop`
- Rama principal de **desarrollo continuo**.
- Recibe los cambios desde ramas `feature/*`.
- Todo cambio debe integrarse mediante Pull Requests.

#### Ramas `feature/*`
- Utilizadas para el desarrollo de nuevas funcionalidades.
- Cada funcionalidad debe desarrollarse en su propia rama.
- Una vez completada, se integran en `develop` mediante Pull Request.

### 🚫 Restricciones de push

- No se permite hacer `push` directo a la rama `main`.
- No se permite desarrollar directamente sobre la rama `develop`.
- Todo desarrollo debe realizarse en ramas `feature/*`.

### 📝 Convención de commits

Los mensajes de commit deben seguir el siguiente formato:
```
<tipo>: <descripción breve>
```
Tipos permitidos:
- feat: nueva funcionalidad
- fix: corrección de errores
- refactor: cambios internos sin modificar funcionalidad
- docs: cambios en documentación

Ejemplos:
- feat: añade sistema de autenticación
- fix: corrige validación de usuario
- refactor: reorganiza servicios de negocio

### 🔍 Revisión de código

Todos los cambios deben integrarse mediante Pull Requests, los cuales son revisados
antes de ser aceptados para garantizar la calidad y estabilidad del proyecto.

# 🐺 Guía de instalación y prueba

## Requisitos previos

- Node.js instalado (para el frontend)
- Java 21 instalado (para el backend)
- Maven instalado o usar el wrapper incluido (`mvnw`)
- IDE compatible: IntelliJ IDEA o Eclipse

---

## 📦 Descarga

En la sección **Releases** del repositorio encontrarás una versión *Early Access* con todos los archivos necesarios para ejecutar el proyecto. Descarga y descomprime el archivo antes de continuar.

---

## ▶️ Ejecución del frontend

1. Abre una terminal y navega hasta la carpeta `frontend`
2. Instala las dependencias (solo la primera vez):
```bash
npm install
```
3. Arranca el servidor de desarrollo:
```bash
npm run dev
```
4. El frontend estará disponible en `http://localhost:5173`

---

## ▶️ Ejecución del backend

Antes de arrancar el backend es necesario configurar las variables de entorno con las credenciales de la base de datos y el secreto JWT.

### Variables necesarias

| Variable | Descripción |
|----------|-------------|
| `DB_URL` | URL de conexión a MySQL |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Clave secreta para firmar los tokens |
| `JWT_EXPIRATION` | Duración del token en segundos |

---

### IntelliJ IDEA

1. Ve a `Run` → `Edit Configurations`
2. Selecciona tu configuración de Spring Boot
3. En el campo **Environment variables** añade las variables separadas por `;`:
```
DB_URL=jdbc:mysql://...;DB_USERNAME=usuario;DB_PASSWORD=contraseña;JWT_SECRET=tu_secret;JWT_EXPIRATION=8640000
```
4. Pulsa **Apply** y luego **Run**

---

### Eclipse

1. Click derecho sobre el proyecto → `Run As` → `Run Configurations`
2. Selecciona tu configuración de Spring Boot
3. Ve a la pestaña **Environment** y pulsa **New** para añadir cada variable:

| Name | Value |
|------|-------|
| `DB_URL` | `jdbc:mysql://...` |
| `DB_USERNAME` | `usuario` |
| `DB_PASSWORD` | `contraseña` |
| `JWT_SECRET` | `tu_secret` |
| `JWT_EXPIRATION` | `8640000` |

4. Pulsa **Apply** y luego **Run**

---

### Línea de comandos

**Windows CMD:**
```cmd
set DB_URL=jdbc:mysql://...
set DB_USERNAME=usuario
set DB_PASSWORD=contraseña
set JWT_SECRET=tu_secret
set JWT_EXPIRATION=8640000
mvn spring-boot:run
```

---

## 🧪 Cómo probar la aplicación

Para facilitar las pruebas, el número mínimo de jugadores para iniciar una partida se ha reducido a 3. Solo necesitas abrir **3 pestañas en modo incógnito** en tu navegador.

### Cuentas de prueba disponibles

Hay cuentas ya creadas listas para usar:

| Usuario | Contraseña |
|---------|------------|
| user1 — user11 | 1234 |

O puedes registrar una cuenta nueva desde la pantalla de inicio.

---

### Flujo de la partida

1. **Crear sala** — uno de los usuarios entra en *Sala de juegos* y pulsa *Crear sala*. Se generará un código único de sala
2. **Unirse** — el resto de usuarios pulsan *Unirse a sala* e introducen el código generado
3. **Asignar narrador** — el creador de la sala selecciona a un jugador de la lista como narrador. Por ahora solo el que crea la sala se seleccionara como narrador.
4. **Iniciar partida** — el creador de la sala pulsa *Comenzar partida* y a cada jugador se le asigna un rol en secreto.
5. **Durante la partida:**
   - Los jugadores disponen de un botón **Mi Rol** para consultar su personaje en cualquier momento
   - El **sol** y la **luna** en la pantalla del narrador son el botón para cambiar entre el día y la noche
   - El narrador gestiona las votaciones y confirma las muertes desde su panel de control. No están desarrolladas estas opciones

#### Variables
```
Estaran junto al enlace de entrega del proyecto o un comentario, debido a seguridad de git.
```

_Posibles errores:_
- El back a sido desarrollado con IntellyJ IDEA el cual incorpora addons para la dependecia de lombok, en el caso de que se use Eclipse es necesario que ejecute el .jar de lombok para instalarse como agente del IDE, sin esta dependecia no cargaran los constructores, getters, setter y propiedades del proyecto. Está posiblemente ubicado en:  _C:\Users\{tuUsuario}\.m2\repository\org\projectlombok\lombok\{version}\lombok-{version}.jar_
