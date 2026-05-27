<?php
require_once __DIR__ . '/config.php';

try {
    // 1. Limpiar base de datos (Orden de borrado por llaves foráneas)
    $pdo->exec("SET FOREIGN_KEY_CHECKS = 0;");
    $pdo->exec("TRUNCATE TABLE progreso_usuario;");
    $pdo->exec("TRUNCATE TABLE pasos;");
    $pdo->exec("TRUNCATE TABLE requisitos;");
    $pdo->exec("TRUNCATE TABLE tramites;");
    $pdo->exec("TRUNCATE TABLE calificaciones;");
    $pdo->exec("TRUNCATE TABLE chat_messages;");
    $pdo->exec("TRUNCATE TABLE password_resets;");
    // No truncamos 'users' por completo para evitar errores de sesión si el ID cambia,
    // pero nos aseguramos de que el usuario principal exista.
    $pdo->exec("DELETE FROM users WHERE email = 'L21270156@tuxtla.tecnm.mx';");
    $pdo->exec("SET FOREIGN_KEY_CHECKS = 1;");

    $pdo->beginTransaction();

    // 2. Insertar usuario principal con ID fijo
    $hash = '$2y$12$z5BqtjBz9EwCe.JoiFylneSkzOyleddpWoK6vMFuueVEMv3QUFoUe'; // MedCer2214$
    $stmtUser = $pdo->prepare("INSERT INTO users (id, name, control_number, email, password_hash, role) VALUES (1, ?, ?, ?, ?, 'admin')");
    $stmtUser->execute(['JULIO ALEJANDRO MEDINA CERVANTES', '21270156', 'L21270156@tuxtla.tecnm.mx', $hash]);
    $userId = 1;

    // 3. Trámite de Reinscripción Actualizado (Enero - Junio 2026)
    $title = 'Reinscripción Enero - Junio 2026';
    $desc = 'Procedimiento para las Reinscripciones Modalidad Presencial';
    $stmtTramite = $pdo->prepare("INSERT INTO tramites (title, description, category) VALUES (?, ?, ?)");
    $stmtTramite->execute([$title, $desc, 'Académico']);
    $tramiteId = $pdo->lastInsertId();

    // Pasos según la imagen
    $pasos = [
        [1, 'Inicio del Proceso (08 al 13 de enero)', 'Accede a estudiantes.tuxtla.tecnm.mx. Menú "Inscripciones" > "Pago referenciado". Imprime y realiza el depósito del 08 al 13 de enero de 2026.'],
        [2, 'Realizar el Pago', 'Realiza tu pago del 08 al 13 de enero. El pago se refleja en 1 día hábil. Transferencia CLABE: 014100655018814176.'],
        [3, 'Toma de Carga', 'Estudiante Regular: 13 al 15 de enero. Estudiante Irregular: 16 al 22 de enero. Consulta tu fecha y hora en tu sesión de estudiante.'],
        [4, 'Validación de Carga', 'Espera la validación por la División de Estudios Profesionales. Inicio de clases: 26 de enero de 2026.']
    ];

    $stmtPaso = $pdo->prepare("INSERT INTO pasos (tramite_id, step_number, title, description) VALUES (?, ?, ?, ?)");
    foreach ($pasos as $p) {
        $stmtPaso->execute([$tramiteId, $p[0], $p[1], $p[2]]);
    }

    // Requisitos actualizados
    $requisitos = [
        ['No tener adeudos pendientes (Biblioteca, Financieros, etc).', 1],
        ['Contar con NIP de acceso vigente.', 1],
        ['Haber realizado la Evaluación Docente del semestre anterior.', 1],
        ['Comprobante de pago referenciado original.', 1]
    ];
    $stmtReq = $pdo->prepare("INSERT INTO requisitos (tramite_id, description, is_mandatory) VALUES (?, ?, ?)");
    foreach ($requisitos as $r) {
        $stmtReq->execute([$tramiteId, $r[0], $r[1]]);
    }

    // 4. Otros trámites de ejemplo
    $stmtTramite->execute(['Servicio Social', 'Trámite para iniciar el servicio social', 'Titulación']);
    $stmtTramite->execute(['Constancia de Estudios', 'Solicitud de constancia oficial', 'Documentos']);

    $pdo->commit();

    echo "<h1>✅ Reinscripción 2026 y Sistema Inicializados</h1>";
    echo "<p>Alumno: JULIO ALEJANDRO MEDINA CERVANTES</p>";
    echo "<p>Los pasos del trámite de reinscripción coinciden ahora con el procedimiento oficial de Enero-Junio 2026.</p>";

} catch (Exception $e) {
    if (isset($pdo) && $pdo->inTransaction()) $pdo->rollBack();
    echo "<h1>❌ Error Crítico:</h1> " . $e->getMessage();
}
