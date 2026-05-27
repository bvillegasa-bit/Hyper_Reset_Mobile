-- ============================================
-- SCRIPT MYSQL PARA HYPER RESET PERFORMANCE
-- Migrado de SQL Server a MySQL
-- Fecha: Mayo 2026
-- ============================================

CREATE DATABASE IF NOT EXISTS HyperReset
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE HyperReset;

-- ============================================
-- TABLA 1: usuario
-- ============================================
CREATE TABLE usuario (
    id_usuario       INT           NOT NULL AUTO_INCREMENT,
    nombres          VARCHAR(100)  NOT NULL,
    apellidos        VARCHAR(100)  NOT NULL,
    correo           VARCHAR(150)  NOT NULL,
    contrasena_hash  VARCHAR(255)  NOT NULL,
    telefono         VARCHAR(20)       NULL,
    rol              VARCHAR(20)   NOT NULL,      -- 'COACH' | 'DEPORTISTA'
    fecha_nacimiento DATE              NULL,
    fecha_registro   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo           TINYINT(1)    NOT NULL DEFAULT 1,

    CONSTRAINT pk_usuario        PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_correo UNIQUE      (correo),
    CONSTRAINT ck_usuario_rol    CHECK       (rol IN ('COACH', 'DEPORTISTA'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 2: coach
-- ============================================
CREATE TABLE coach (
    id_coach     INT           NOT NULL AUTO_INCREMENT,
    id_usuario   INT           NOT NULL,
    especialidad VARCHAR(100)      NULL,
    descripcion  TEXT              NULL,

    CONSTRAINT pk_coach         PRIMARY KEY (id_coach),
    CONSTRAINT uq_coach_usuario UNIQUE      (id_usuario),
    CONSTRAINT fk_coach_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 3: deportista
-- ============================================
CREATE TABLE deportista (
    id_deportista    INT          NOT NULL AUTO_INCREMENT,
    id_usuario       INT          NOT NULL,
    id_coach         INT              NULL,
    deporte          VARCHAR(100)     NULL,
    nivel_deportista VARCHAR(20)      NULL,     -- 'AMATEUR' | 'SEMIPROFESIONAL' | 'PROFESIONAL'
    estado_deportista VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

    CONSTRAINT pk_deportista         PRIMARY KEY (id_deportista),
    CONSTRAINT uq_deportista_usuario UNIQUE      (id_usuario),
    CONSTRAINT ck_deportista_nivel   CHECK       (nivel_deportista  IN ('AMATEUR','SEMIPROFESIONAL','PROFESIONAL')),
    CONSTRAINT ck_deportista_estado  CHECK       (estado_deportista IN ('ACTIVO','PENDIENTE')),
    CONSTRAINT fk_deportista_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_deportista_coach   FOREIGN KEY (id_coach)
        REFERENCES coach(id_coach)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 4: test_fisico
-- ============================================
CREATE TABLE test_fisico (
    id_test_fisico                 INT           NOT NULL AUTO_INCREMENT,
    id_deportista                  INT           NOT NULL,
    id_coach                       INT           NOT NULL,
    fecha_ejecucion                DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lugar                          VARCHAR(150)      NULL,
    peso_deportista                DECIMAL(5,2)      NULL,
    estatura_parado_deportista     DECIMAL(5,2)      NULL,
    estatura_sentado_deportista    DECIMAL(5,2)      NULL,
    antecedentes_medicos           TEXT              NULL,
    estado_test                    VARCHAR(20)   NOT NULL DEFAULT 'EN_PROGRESO',

    CONSTRAINT pk_test_fisico  PRIMARY KEY (id_test_fisico),
    CONSTRAINT ck_test_estado  CHECK       (estado_test IN ('EN_PROGRESO','COMPLETADO')),
    CONSTRAINT fk_test_deportista FOREIGN KEY (id_deportista)
        REFERENCES deportista(id_deportista)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_test_coach FOREIGN KEY (id_coach)
        REFERENCES coach(id_coach)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 5: resultado_test
-- ============================================
CREATE TABLE resultado_test (
    id_resultado    INT           NOT NULL AUTO_INCREMENT,
    id_test_fisico  INT           NOT NULL,
    tipo_test       VARCHAR(30)   NOT NULL,
    valor_obtenido  DECIMAL(8,3)  NOT NULL,
    unidad_medicion VARCHAR(20)   NOT NULL,
    calificacion    VARCHAR(20)       NULL,
    observaciones   TEXT              NULL,

    CONSTRAINT pk_resultado_test  PRIMARY KEY (id_resultado),
    CONSTRAINT ck_resultado_tipo  CHECK (tipo_test IN (
                                        'ILLINOIS',
                                        'FLEXION_CODOS',
                                        'VELOCIDAD_20M',
                                        'VELOCIDAD_REACCION',
                                        'SALTO_HORIZONTAL',
                                        'FLEXION_TRONCO',
                                        'DINAMOMETRIA',
                                        'ANDERSEN'
                                    )),
    CONSTRAINT ck_resultado_calif CHECK (calificacion IN ('EXCELENTE','BUENO','REGULAR','DEFICIENTE')),
    CONSTRAINT fk_resultado_test  FOREIGN KEY (id_test_fisico)
        REFERENCES test_fisico(id_test_fisico)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 6: cita
-- ============================================
CREATE TABLE cita (
    id_cita          INT           NOT NULL AUTO_INCREMENT,
    id_deportista    INT           NOT NULL,
    id_coach         INT           NOT NULL,
    fecha_hora       DATETIME      NOT NULL,
    tipo_sesion      VARCHAR(100)  NOT NULL,
    duracion_sesion  INT           NOT NULL DEFAULT 60,
    estado           VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    notas            TEXT              NULL,

    CONSTRAINT pk_cita             PRIMARY KEY (id_cita),
    CONSTRAINT ck_cita_estado      CHECK       (estado IN ('PENDIENTE','CONFIRMADA','CANCELADA','COMPLETADA')),
    CONSTRAINT fk_cita_deportista  FOREIGN KEY (id_deportista)
        REFERENCES deportista(id_deportista)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_cita_coach       FOREIGN KEY (id_coach)
        REFERENCES coach(id_coach)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 7: mensaje
-- ============================================
CREATE TABLE mensaje (
    id_mensaje       INT           NOT NULL AUTO_INCREMENT,
    id_remitente     INT           NOT NULL,
    id_destinatario  INT           NOT NULL,
    contenido_mmensaje  TEXT       NOT NULL,
    tipo_mensaje     VARCHAR(20)   NOT NULL DEFAULT 'TEXTO',
    fecha_envio      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_leido     TINYINT(1)    NOT NULL DEFAULT 0,

    CONSTRAINT pk_mensaje              PRIMARY KEY (id_mensaje),
    CONSTRAINT ck_mensaje_tipo         CHECK       (tipo_mensaje IN ('TEXTO','IMAGEN','ARCHIVO')),
    CONSTRAINT fk_mensaje_remitente    FOREIGN KEY (id_remitente)
        REFERENCES usuario(id_usuario)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_mensaje_destinatario FOREIGN KEY (id_destinatario)
        REFERENCES usuario(id_usuario)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índice para búsqueda de conversaciones
CREATE INDEX idx_mensaje_conversacion
    ON mensaje(id_remitente, id_destinatario, fecha_envio);

-- ============================================
-- TABLA 8: reporte
-- ============================================
CREATE TABLE reporte (
    id_reporte             INT           NOT NULL AUTO_INCREMENT,
    id_test_fisico         INT           NOT NULL,
    id_coach               INT           NOT NULL,
    fecha_generacion       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones_reporte  TEXT              NULL,
    recomendaciones_reporte TEXT             NULL,
    ruta_pdf_reporte       VARCHAR(500)      NULL,

    CONSTRAINT pk_reporte       PRIMARY KEY (id_reporte),
    CONSTRAINT uq_reporte_test  UNIQUE      (id_test_fisico),   -- 1 reporte por test
    CONSTRAINT fk_reporte_test  FOREIGN KEY (id_test_fisico)
        REFERENCES test_fisico(id_test_fisico)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_reporte_coach FOREIGN KEY (id_coach)
        REFERENCES coach(id_coach)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA 9: material
-- ============================================
CREATE TABLE material (
    id_material          INT           NOT NULL AUTO_INCREMENT,
    id_coach             INT           NOT NULL,
    titulo_material      VARCHAR(200)  NOT NULL,
    descripcion_material TEXT              NULL,
    tipo_material        VARCHAR(20)   NOT NULL,
    url_contenido        VARCHAR(500)  NOT NULL,
    fecha_subida         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_material      PRIMARY KEY (id_material),
    CONSTRAINT ck_material_tipo CHECK       (tipo_material IN ('VIDEO','DOCUMENTO','IMAGEN','ENLACE')),
    CONSTRAINT fk_material_coach FOREIGN KEY (id_coach)
        REFERENCES coach(id_coach)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
