<?php
require_once __DIR__ . '/config.php';

try {
    // 1. Crear tabla de calificaciones si no existe (Schema completo)
    $pdo->exec("DROP TABLE IF EXISTS calificaciones;");
    $pdo->exec("CREATE TABLE calificaciones (
        id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
        user_id INT UNSIGNED NOT NULL,
        periodo VARCHAR(100) NOT NULL,
        clave VARCHAR(20) NOT NULL,
        materia VARCHAR(200) NOT NULL,
        creditos INT NOT NULL,
        calificacion VARCHAR(10) NOT NULL,
        evaluacion VARCHAR(100) NOT NULL,
        observaciones TEXT NULL,
        CONSTRAINT fk_calificaciones_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;");

    // 2. Limpiar y Crear tu usuario con nombre completo
    $pdo->prepare("DELETE FROM users WHERE email = ? OR control_number = ?")->execute(['L21270156@tuxtla.tecnm.mx', '21270156']);

    $hash = password_hash('MedCer2214$', PASSWORD_BCRYPT, ['cost' => 12]);
    $stmt = $pdo->prepare("INSERT INTO users (id, name, control_number, email, password_hash, role) VALUES (1, ?, ?, ?, ?, 'admin')");
    $stmt->execute(['JULIO ALEJANDRO MEDINA CERVANTES', '21270156', 'L21270156@tuxtla.tecnm.mx', $hash]);
    $userId = 1;

    // 3. Insertar tu historial académico real
    $materias = [
        ['ENERO-JUNIO/2021', 'ACF0901', 'CALC.DIFER.', 5, '70', 'Evaluacion Ordinaria'],
        ['ENERO-JUNIO/2021', 'AED1285', 'FUND. DE PROG.', 5, '79', 'Evaluacion Ordinaria'],
        ['ENERO-JUNIO/2021', 'ACA0907', 'TALL.DE ETICA', 4, '94', 'Evaluacion Ordinaria'],
        ['AGOSTO-DICIEMBRE/2024', 'SCD1015', 'LENG.Y AUTOM.I', 5, '70', 'Evaluacion Complementaria'],
        ['ENERO-JUNIO/2025', 'SCC1014', 'LENGUAJES DE INTERFAZ', 4, '97', 'Evaluacion Ordinaria'],
        ['AGOSTO-DICIEMBRE/2025', 'SCC1012', 'INTELIGENCIA ARTIFICIAL', 4, '100', 'Evaluacion Ordinaria']
    ];

    $stmt = $pdo->prepare("INSERT INTO calificaciones (user_id, periodo, clave, materia, creditos, calificacion, evaluacion) VALUES (?, ?, ?, ?, ?, ?, ?)");
    foreach ($materias as $m) {
        $stmt->execute([$userId, $m[0], $m[1], $m[2], $m[3], $m[4], $m[5]]);
    }

    echo "<h1>✅ Configuración de Base de Datos Finalizada</h1>";
    echo "<p>Usuario: JULIO ALEJANDRO MEDINA CERVANTES</p>";
    echo "<p>ID Generado: $userId</p>";
    echo "<p>Historial académico cargado (Kardex).</p>";
} catch (Exception $e) {
    echo "<h1>❌ Error:</h1> " . $e->getMessage();
}
