CREATE DATABASE IF NOT EXISTS clebot_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE clebot_db;

CREATE TABLE IF NOT EXISTS users (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  control_number VARCHAR(30) NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('student', 'external', 'admin') NOT NULL DEFAULT 'student',
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS password_resets (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NOT NULL,
  token_hash CHAR(64) NOT NULL,
  expires_at DATETIME NOT NULL,
  used_at DATETIME NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_password_resets_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE,
  INDEX idx_password_resets_token_hash (token_hash),
  INDEX idx_password_resets_user_id (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS support_tickets (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NULL,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL,
  category VARCHAR(80) NOT NULL,
  description TEXT NOT NULL,
  status ENUM('abierto', 'en_proceso', 'cerrado') NOT NULL DEFAULT 'abierto',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_support_tickets_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE SET NULL,
  INDEX idx_support_tickets_email (email),
  INDEX idx_support_tickets_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS chat_messages (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NULL,
  sender ENUM('user', 'bot') NOT NULL,
  message TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_chat_messages_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE SET NULL,
  INDEX idx_chat_messages_user_id (user_id),
  INDEX idx_chat_messages_created_at (created_at)
) ENGINE=InnoDB;

-- Tabla de carreras
CREATE TABLE IF NOT EXISTS carreras (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL UNIQUE,
  abreviatura VARCHAR(10) NULL
) ENGINE=InnoDB;

-- Tabla de materias de la retícula oficial
CREATE TABLE IF NOT EXISTS reticulas (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  carrera_id INT UNSIGNED NOT NULL,
  semestre INT NOT NULL,
  clave VARCHAR(20) NOT NULL,
  nombre VARCHAR(200) NOT NULL,
  creditos INT NOT NULL,
  CONSTRAINT fk_reticula_carrera
    FOREIGN KEY (carrera_id) REFERENCES carreras(id)
    ON DELETE CASCADE,
  INDEX idx_reticula_carrera (carrera_id),
  INDEX idx_reticula_semestre (semestre)
) ENGINE=InnoDB;

-- Insertar carreras base
INSERT IGNORE INTO carreras (id, nombre, abreviatura) VALUES
(1, 'Ingeniería en Sistemas Computacionales', 'ISC'),
(2, 'Ingeniería Mecatrónica', 'IMKT'),
(3, 'Ingeniería Industrial', 'IND'),
(4, 'Ingeniería en Logística', 'ILOG'),
(5, 'Ingeniería en Gestión Empresarial', 'IGE');

-- Insertar retícula de ISC (Ejemplo basado en setup_kardex_full.php)
INSERT IGNORE INTO reticulas (carrera_id, semestre, clave, nombre, creditos) VALUES
(1, 1, 'ACF0901', 'Cálculo Diferencial', 5),
(1, 1, 'AED1285', 'Fundamentos de Programación', 5),
(1, 1, 'ACA0907', 'Taller de Ética', 4),
(1, 1, 'AEF1041', 'Matemáticas Discretas', 5),
(1, 1, 'SCH1024', 'Taller de Administración', 4),
(1, 1, 'ACC0906', 'Fundamentos de Investigación', 4),
(1, 2, 'ACF0902', 'Cálculo Integral', 5),
(1, 2, 'SCD1020', 'Programación Orientada a Objetos', 5),
(1, 8, 'SCD1012', 'Inteligencia Artificial', 5),
(1, 8, 'AEW-2204', 'Programación de Dispositivos Móviles Android', 5);

-- Módulo de Trámites (Trami-Tec)
CREATE TABLE IF NOT EXISTS tramites (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  category VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_tramites_category (category)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS requisitos (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  tramite_id INT UNSIGNED NOT NULL,
  description TEXT NOT NULL,
  is_mandatory TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT fk_requisitos_tramite
    FOREIGN KEY (tramite_id) REFERENCES tramites(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pasos (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  tramite_id INT UNSIGNED NOT NULL,
  step_number INT UNSIGNED NOT NULL,
  title VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  CONSTRAINT fk_pasos_tramite
    FOREIGN KEY (tramite_id) REFERENCES tramites(id)
    ON DELETE CASCADE,
  INDEX idx_pasos_tramite_id (tramite_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS progreso_usuario (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NOT NULL,
  tramite_id INT UNSIGNED NOT NULL,
  paso_id INT UNSIGNED NOT NULL,
  is_completed TINYINT(1) NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_progreso_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_progreso_tramite
    FOREIGN KEY (tramite_id) REFERENCES tramites(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_progreso_paso
    FOREIGN KEY (paso_id) REFERENCES pasos(id)
    ON DELETE CASCADE,
  UNIQUE KEY unique_user_paso (user_id, paso_id)
) ENGINE=InnoDB;

-- Insertar datos de prueba para trámites
INSERT INTO tramites (id, title, description, category) VALUES 
(1, 'Reinscripción', 'Proceso de reinscripción al nuevo semestre', 'Académico'),
(2, 'Servicio Social', 'Trámite para iniciar el servicio social', 'Titulación'),
(3, 'Constancia de Estudios', 'Solicitud de constancia oficial de estudios', 'Documentos');

INSERT INTO requisitos (tramite_id, description, is_mandatory) VALUES 
(1, 'No tener adeudos en biblioteca', 1),
(1, 'Haber pagado la cuota de reinscripción', 1),
(2, 'Tener el 70% de créditos aprobados', 1),
(3, 'Estar inscrito en el semestre actual', 1),
(3, 'Fotografía tamaño infantil (Opcional)', 0);

INSERT INTO pasos (tramite_id, step_number, title, description) VALUES 
(1, 1, 'Pagar cuota', 'Realizar el pago referenciado en el banco.'),
(1, 2, 'Entregar comprobante', 'Subir el comprobante de pago en el portal.'),
(1, 3, 'Seleccionar materias', 'Elegir las materias en el sistema de carga académica.'),
(2, 1, 'Asistir a plática', 'Acudir a la plática de inducción al servicio social.'),
(2, 2, 'Elegir dependencia', 'Seleccionar el lugar donde se realizará el servicio.'),
(3, 1, 'Solicitar en servicios escolares', 'Acudir a ventanilla y pedir la constancia.'),
(3, 2, 'Recoger documento', 'Volver al día siguiente para recoger la constancia firmada y sellada.');

-- Insertar usuario de prueba (Admin)
INSERT INTO users (name, control_number, email, password_hash, role) VALUES
('JULIO ALEJANDRO MEDINA CERVANTES', '21270156', 'L21270156@tuxtla.tecnm.mx', '$2y$12$z5BqtjBz9EwCe.JoiFylneSkzOyleddpWoK6vMFuueVEMv3QUFoUe', 'admin');

-- Tabla de historial académico (Kardex)
CREATE TABLE IF NOT EXISTS calificaciones (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NOT NULL,
  periodo VARCHAR(100) NOT NULL,
  clave VARCHAR(20) NOT NULL,
  materia VARCHAR(200) NOT NULL,
  creditos INT NOT NULL,
  calificacion VARCHAR(10) NOT NULL,
  evaluacion VARCHAR(100) NOT NULL,
  observaciones TEXT NULL,
  CONSTRAINT fk_calificaciones_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Insertar datos académicos reales
INSERT INTO calificaciones (user_id, periodo, clave, materia, creditos, calificacion, evaluacion) VALUES
(LAST_INSERT_ID(), 'ENERO-JUNIO/2021', 'ACF0901', 'CALC.DIFER.', 5, '70', 'Evaluacion Ordinaria'),
(LAST_INSERT_ID(), 'ENERO-JUNIO/2021', 'AED1285', 'FUND. DE PROG.', 5, '79', 'Evaluacion Ordinaria'),
(LAST_INSERT_ID(), 'ENERO-JUNIO/2021', 'ACA0907', 'TALL.DE ETICA', 4, '94', 'Evaluacion Ordinaria'),
(LAST_INSERT_ID(), 'AGOSTO-DICIEMBRE/2021', 'ACF0902', 'CALC.INTEGRAL', 5, '87', 'Evaluacion Complementaria'),
(LAST_INSERT_ID(), 'AGOSTO-DICIEMBRE/2025', 'SCC1012', 'INTELIGENCIA ARTIFICIAL', 4, '100', 'Evaluacion Ordinaria');
