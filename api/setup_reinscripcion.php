<?php
require_once __DIR__ . '/config.php';

try {
    $pdo->beginTransaction();

    // 1. Buscar o crear el trámite de Reinscripción
    $stmt = $pdo->prepare("SELECT id FROM tramites WHERE title LIKE '%Reinscripción%' LIMIT 1");
    $stmt->execute();
    $tramite = $stmt->fetch();

    if ($tramite) {
        $tramiteId = $tramite['id'];
        $pdo->prepare("UPDATE tramites SET title = 'Reinscripción Enero - Junio 2026', description = 'Procedimiento para las Reinscripciones Modalidad Presencial' WHERE id = ?")->execute([$tramiteId]);
    } else {
        $stmt = $pdo->prepare("INSERT INTO tramites (title, description, category) VALUES (?, ?, ?)");
        $stmt->execute(['Reinscripción Enero - Junio 2026', 'Procedimiento para las Reinscripciones Modalidad Presencial', 'Académico']);
        $tramiteId = $pdo->lastInsertId();
    }

    // 2. Limpiar pasos anteriores
    $pdo->prepare("DELETE FROM pasos WHERE tramite_id = ?")->execute([$tramiteId]);

    // 3. Insertar nuevos pasos basados en la imagen
    $pasos = [
        [
            1,
            'Inicio del Proceso (08 al 13 de enero)',
            'Accede a estudiantes.tuxtla.tecnm.mx. Genera tu pago referenciado en el menú "Inscripciones" > "Pago referenciado". Imprime y realiza el depósito.'
        ],
        [
            2,
            'Realizar el Pago',
            'Realiza tu pago del 08 al 13 de enero. Puede ser en ventanilla bancaria o transferencia (CLABE: 014100655018814176). El pago se refleja en 1 día hábil.'
        ],
        [
            3,
            'Toma de Carga Académica',
            'Estudiantes Regulares: 13 al 15 de enero. Estudiantes Irregulares: 16 al 22 de enero. Consulta tu fecha y hora en el portal. Programación: Vie 16 (2-3 sem), Lun 19 (4-5 sem), Mar 20 (6-7 sem), Mié 21 (8-9 sem).'
        ],
        [
            4,
            'Validación de Carga',
            'Una vez realizada la toma de carga, espera la validación por la División de Estudios Profesionales para descargar e imprimir tu carga académica oficial.'
        ]
    ];

    $stmt = $pdo->prepare("INSERT INTO pasos (tramite_id, step_number, title, description) VALUES (?, ?, ?, ?)");
    foreach ($pasos as $p) {
        $stmt->execute([$tramiteId, $p[0], $p[1], $p[2]]);
    }

    // 4. Actualizar requisitos
    $pdo->prepare("DELETE FROM requisitos WHERE tramite_id = ?")->execute([$tramiteId]);
    $requisitos = [
        'No tener adeudos pendientes (financieros o de documentos).',
        'Contar con NIP de acceso al portal de estudiantes.',
        'Haber realizado la evaluación docente.',
        'No haber reprobado ninguna materia el semestre anterior (para ser regular).'
    ];

    $stmt = $pdo->prepare("INSERT INTO requisitos (tramite_id, description, is_mandatory) VALUES (?, ?, 1)");
    foreach ($requisitos as $r) {
        $stmt->execute([$tramiteId, $r]);
    }

    $pdo->commit();
    echo "<h1>✅ Proceso de Reinscripción 2026 Actualizado</h1>";
    echo "<p>Se han cargado los 4 pasos principales y los requisitos actualizados.</p>";

} catch (Exception $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    echo "<h1>❌ Error:</h1> " . $e->getMessage();
}
