DROP DATABASE IF EXISTS Skhatoll;
CREATE DATABASE Skhatoll;
USE Skhatoll;

-- ============================================================
-- 1. USUARIOS: Cuentas de los jugadores
-- ============================================================
CREATE TABLE usuarios (
    id_usuario   INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(50)  NOT NULL UNIQUE,
    codigo_uuid  VARCHAR(36)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL
);

-- ============================================================
-- 2. ROLES: Definición estática de personajes
-- ============================================================
CREATE TABLE roles (
    id_rol      INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    bando       ENUM('lobo', 'aldea', 'narrador') NOT NULL
);

-- ============================================================
-- 3. SALAS: Cabecera de la partida
-- ============================================================
CREATE TABLE salas (
    id_sala        INT AUTO_INCREMENT PRIMARY KEY,
    id_narrador    INT          NOT NULL,
    codigo_sala    VARCHAR(10)  NOT NULL UNIQUE,
    estado_sala    ENUM('CREADA', 'INICIADA', 'CERRADA') DEFAULT 'CREADA',
    estado_dia     ENUM('DIA', 'NOCHE')                 DEFAULT 'DIA',
    ronda_actual   INT          DEFAULT 1,
    min_jugadores  INT          DEFAULT 8,
    max_jugadores  INT          DEFAULT 28,
    fecha_creacion TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_narrador) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ============================================================
-- 4. SALA_USUARIOS: Jugadores dentro de una sala y su estado
-- ============================================================
CREATE TABLE sala_usuarios (
    id_sala_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    id_sala          INT NOT NULL,
    id_usuario       INT NOT NULL,
    id_rol           INT,
    esta_vivo        BOOLEAN DEFAULT TRUE,
    muerte_confirmada BOOLEAN DEFAULT FALSE,

    UNIQUE (id_sala, id_usuario),

    FOREIGN KEY (id_sala)    REFERENCES salas(id_sala)       ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_rol)     REFERENCES roles(id_rol)        ON DELETE SET NULL
);

-- ============================================================
-- 5. HABILIDADES_SALA: Habilidades de cada jugador en la sala
--    Se inserta una fila por habilidad al asignar el rol.
-- ============================================================
CREATE TABLE habilidades_sala (
    id_habilidad    INT AUTO_INCREMENT PRIMARY KEY,
    id_sala_usuario INT         NOT NULL,
    nombre          VARCHAR(50) NOT NULL,  -- ej: 'pocion_vida', 'pocion_muerte', 'disparo'
    usada           BOOLEAN     DEFAULT FALSE,

    UNIQUE (id_sala_usuario, nombre),

    FOREIGN KEY (id_sala_usuario) REFERENCES sala_usuarios(id_sala_usuario) ON DELETE CASCADE
);

-- ============================================================
-- 6. ENAMORADOS: Par de jugadores vinculados por Cupido
--    Una sola fila por sala (Cupido solo actúa una vez).
-- ============================================================
CREATE TABLE enamorados (
    id_sala      INT NOT NULL,
    id_usuario_1 INT NOT NULL,
    id_usuario_2 INT NOT NULL,

    PRIMARY KEY (id_sala),

    FOREIGN KEY (id_sala)      REFERENCES salas(id_sala)       ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_1) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_2) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ============================================================
-- 7. SESIONES_VOTACION: Control de apertura y cierre de votos
--    El narrador abre y cierra cada sesión explícitamente.
-- ============================================================
CREATE TABLE sesiones_votacion (
    id_sesion    INT AUTO_INCREMENT PRIMARY KEY,
    id_sala      INT NOT NULL,
    tipo         ENUM('DIA', 'LOBOS', 'HABILIDAD') NOT NULL,
    ronda        INT NOT NULL,
    abierta      BOOLEAN   DEFAULT TRUE,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP NULL,

    FOREIGN KEY (id_sala) REFERENCES salas(id_sala) ON DELETE CASCADE
);

-- ============================================================
-- 8. VOTOS: Historial de votos vinculado a la sesión
-- ============================================================
CREATE TABLE votos (
    id_voto    INT AUTO_INCREMENT PRIMARY KEY,
    id_sesion  INT NOT NULL,
    id_votante INT NOT NULL,
    id_objetivo INT NOT NULL,
    fecha_voto TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (id_sesion, id_votante),   -- un votante, un voto por sesión

    FOREIGN KEY (id_sesion)   REFERENCES sesiones_votacion(id_sesion)  ON DELETE CASCADE,
    FOREIGN KEY (id_votante)  REFERENCES usuarios(id_usuario)          ON DELETE CASCADE,
    FOREIGN KEY (id_objetivo) REFERENCES usuarios(id_usuario)          ON DELETE CASCADE
);

-- ============================================================
-- DATOS INICIALES: Roles del juego
-- ============================================================
INSERT INTO roles (nombre, descripcion, bando) VALUES
('Lobo', 'Depredador principal del juego', 'lobo'),
('Aldeano', 'Jugador sin habilidades especiales', 'aldea'),
('Vidente', 'Puede revelar información', 'aldea'),
('Cazador', 'Puede contraatacar al morir', 'aldea'),
('Bruja', 'Puede revivir o eliminar', 'aldea'),
('Niño salvaje', 'Puede transformarse en lobo', 'aldea'),
('Cupido', 'Puede enamorar jugadores', 'aldea'),
('Niña', 'Puede espiar', 'aldea');

-- ============================================================
-- NOTAS DE USO
-- ============================================================
-- Al asignar rol 'Bruja' en sala_usuarios, insertar en habilidades_sala:
--   ('pocion_vida',  usada = FALSE)
--   ('pocion_muerte', usada = FALSE)
--
-- Al asignar rol 'Cazador' en sala_usuarios, insertar en habilidades_sala:
--   ('disparo', usada = FALSE)
--
-- Al asignar rol 'Cupido' en sala_usuarios, insertar en habilidades_sala:
--   ('flechazo', usada = FALSE)
--
-- Al asignar rol 'Vidente' en sala_usuarios, no hay habilidad de un solo uso:
--   la consulta nocturna es ilimitada, se gestiona por sesión de votación tipo 'HABILIDAD'.
