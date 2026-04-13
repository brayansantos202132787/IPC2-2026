CREATE DATABASE IF NOT EXISTS horizontes_sin_limites
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE horizontes_sin_limites;

CREATE TABLE rol (
    id_rol      INT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id_rol)
);

CREATE TABLE usuario (
    id_usuario  INT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    id_rol      INT          NOT NULL,
    activo      TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id_usuario),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
);

CREATE TABLE destino (
    id_destino      INT           NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(150)  NOT NULL UNIQUE,
    pais            VARCHAR(100)  NOT NULL,
    descripcion     TEXT,
    clima           VARCHAR(255),
    mejor_epoca     VARCHAR(255),
    imagen_url      VARCHAR(500),
    PRIMARY KEY (id_destino)
);

CREATE TABLE tipo_proveedor (
    id_tipo     INT         NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_tipo)
);

CREATE TABLE proveedor (
    id_proveedor    INT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(150) NOT NULL UNIQUE,
    id_tipo         INT          NOT NULL,
    pais            VARCHAR(100) NOT NULL,
    contacto        VARCHAR(255),
    PRIMARY KEY (id_proveedor),
    CONSTRAINT fk_proveedor_tipo FOREIGN KEY (id_tipo) REFERENCES tipo_proveedor (id_tipo)
);

CREATE TABLE paquete (
    id_paquete      INT             NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(200)    NOT NULL UNIQUE,
    id_destino      INT             NOT NULL,
    duracion_dias   INT             NOT NULL,
    descripcion     TEXT,
    precio_venta    DECIMAL(12, 2)  NOT NULL,
    capacidad       INT             NOT NULL,
    activo          TINYINT(1)      NOT NULL DEFAULT 1,
    PRIMARY KEY (id_paquete),
    CONSTRAINT fk_paquete_destino FOREIGN KEY (id_destino) REFERENCES destino (id_destino)
);

CREATE TABLE servicio_paquete (
    id_servicio     INT             NOT NULL AUTO_INCREMENT,
    id_paquete      INT             NOT NULL,
    id_proveedor    INT             NOT NULL,
    descripcion     VARCHAR(500)    NOT NULL,
    costo_proveedor DECIMAL(12, 2)  NOT NULL,
    PRIMARY KEY (id_servicio),
    CONSTRAINT fk_servicio_paquete     FOREIGN KEY (id_paquete)   REFERENCES paquete   (id_paquete),
    CONSTRAINT fk_servicio_proveedor   FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
);

CREATE TABLE cliente (
    id_cliente      INT          NOT NULL AUTO_INCREMENT,
    dpi             VARCHAR(20)  NOT NULL UNIQUE,
    nombre          VARCHAR(200) NOT NULL,
    fecha_nac       DATE         NOT NULL,
    telefono        VARCHAR(20),
    email           VARCHAR(150),
    nacionalidad    VARCHAR(100),
    PRIMARY KEY (id_cliente)
);

CREATE TABLE estado_reservacion (
    id_estado   INT         NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_estado)
);

CREATE TABLE reservacion (
    id_reservacion      INT             NOT NULL AUTO_INCREMENT,
    numero_reservacion  VARCHAR(20)     NOT NULL UNIQUE,
    id_paquete          INT             NOT NULL,
    id_usuario          INT             NOT NULL,
    fecha_creacion      DATE            NOT NULL,
    fecha_viaje         DATE            NOT NULL,
    cantidad_pasajeros  INT             NOT NULL,
    costo_total         DECIMAL(12, 2)  NOT NULL,
    id_estado           INT             NOT NULL,
    PRIMARY KEY (id_reservacion),
    CONSTRAINT fk_reservacion_paquete  FOREIGN KEY (id_paquete)  REFERENCES paquete             (id_paquete),
    CONSTRAINT fk_reservacion_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuario             (id_usuario),
    CONSTRAINT fk_reservacion_estado   FOREIGN KEY (id_estado)   REFERENCES estado_reservacion  (id_estado)
);

CREATE TABLE reservacion_pasajero (
    id_reservacion  INT NOT NULL,
    id_cliente      INT NOT NULL,
    PRIMARY KEY (id_reservacion, id_cliente),
    CONSTRAINT fk_rp_reservacion   FOREIGN KEY (id_reservacion) REFERENCES reservacion (id_reservacion),
    CONSTRAINT fk_rp_cliente       FOREIGN KEY (id_cliente)     REFERENCES cliente     (id_cliente)
);

CREATE TABLE metodo_pago (
    id_metodo   INT         NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_metodo)
);

CREATE TABLE pago (
    id_pago         INT             NOT NULL AUTO_INCREMENT,
    id_reservacion  INT             NOT NULL,
    monto           DECIMAL(12, 2)  NOT NULL,
    id_metodo       INT             NOT NULL,
    fecha_pago      DATE            NOT NULL,
    PRIMARY KEY (id_pago),
    CONSTRAINT fk_pago_reservacion  FOREIGN KEY (id_reservacion) REFERENCES reservacion (id_reservacion),
    CONSTRAINT fk_pago_metodo       FOREIGN KEY (id_metodo)      REFERENCES metodo_pago (id_metodo)
);

CREATE TABLE cancelacion (
    id_cancelacion      INT             NOT NULL AUTO_INCREMENT,
    id_reservacion      INT             NOT NULL UNIQUE,
    fecha_cancelacion   DATE            NOT NULL,
    monto_reembolsado   DECIMAL(12, 2)  NOT NULL,
    porcentaje_reembolso DECIMAL(5, 2)  NOT NULL,
    perdida_agencia     DECIMAL(12, 2)  NOT NULL,
    PRIMARY KEY (id_cancelacion),
    CONSTRAINT fk_cancelacion_reservacion FOREIGN KEY (id_reservacion) REFERENCES reservacion (id_reservacion)
);

CREATE TABLE log_carga (
    id_log      INT          NOT NULL AUTO_INCREMENT,
    id_usuario  INT          NOT NULL,
    fecha_carga DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    archivo     VARCHAR(255),
    total       INT          NOT NULL DEFAULT 0,
    exitosos    INT          NOT NULL DEFAULT 0,
    errores     INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id_log),
    CONSTRAINT fk_log_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE log_error_carga (
    id_error    INT          NOT NULL AUTO_INCREMENT,
    id_log      INT          NOT NULL,
    linea       INT,
    tipo_error  VARCHAR(20)  NOT NULL,
    descripcion TEXT         NOT NULL,
    PRIMARY KEY (id_error),
    CONSTRAINT fk_error_log FOREIGN KEY (id_log) REFERENCES log_carga (id_log)
);

INSERT INTO rol (nombre) VALUES
    ('Atencion al Cliente'),
    ('Operaciones'),
    ('Administrador');

INSERT INTO tipo_proveedor (nombre) VALUES
    ('Aerolinea'),
    ('Hotel'),
    ('Tour'),
    ('Traslado'),
    ('Otro');

INSERT INTO estado_reservacion (nombre) VALUES
    ('Pendiente'),
    ('Confirmada'),
    ('Cancelada'),
    ('Completada');

INSERT INTO metodo_pago (nombre) VALUES
    ('Efectivo'),
    ('Tarjeta'),
    ('Transferencia');
