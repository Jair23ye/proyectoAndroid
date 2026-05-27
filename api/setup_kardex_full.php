<?php
require_once __DIR__ . '/config.php';

try {
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

    $email = 'L21270156@tuxtla.tecnm.mx';
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user) {
        $hash = password_hash('MedCer2214$', PASSWORD_BCRYPT);
        $stmt = $pdo->prepare("INSERT INTO users (name, control_number, email, password_hash, role) VALUES (?, ?, ?, ?, 'admin')");
        $stmt->execute(['JULIO ALEJANDRO MEDINA CERVANTES', '21270156', $email, $hash]);
        $userId = $pdo->lastInsertId();
    } else {
        $userId = $user['id'];
    }

    $materias = [
        // SEMESTRE 1
        ['AGOSTO-DICIEMBRE/2021', 'ACF0901', 'CALCULO DIFERENCIAL', 5, '70', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2021', 'AED1285', 'FUND. DE PROGRAMACIÓN', 5, '79', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2021', 'ACA0907', 'TALLER DE ÉTICA', 4, '94', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2021', 'AEF1041', 'MATEMÁTICAS DISCRETAS', 5, '85', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2021', 'SCH1024', 'TALLER DE ADMON.', 4, '88', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2021', 'ACC0906', 'FUND. DE INVESTIGACIÓN', 4, '90', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2021', 'TUT0901', 'TUTORIA 1', 0, 'AC', 'Ordinaria'],

        // SEMESTRE 2
        ['ENERO-JUNIO/2022', 'ACF0902', 'CÁLCULO INTEGRAL', 5, '72', 'Ordinaria'],
        ['ENERO-JUNIO/2022', 'SCD1020', 'PROG. ORIENTADA A OBJETOS', 5, '94', 'Ordinaria'],
        ['ENERO-JUNIO/2022', 'AEC1008', 'CONTABILIDAD FINANCIERA', 4, '95', 'Ordinaria'],
        ['ENERO-JUNIO/2022', 'AEC1058', 'QUÍMICA', 4, '80', 'Ordinaria'],
        ['ENERO-JUNIO/2022', 'ACF0904', 'ÁLGEBRA LINEAL', 5, '80', 'Ordinaria'],
        ['ENERO-JUNIO/2022', 'AEC1032', 'FÍSICA GENERAL', 4, '85', 'Ordinaria'],
        ['ENERO-JUNIO/2022', 'ACD0908', 'DESARROLLO SUSTENTABLE', 5, '100', 'Ordinaria'],
        ['ENERO-JUNIO/2022', 'TUT0902', 'TUTORIA 2', 0, 'AC', 'Ordinaria'],

        // SEMESTRE 3
        ['AGOSTO-DICIEMBRE/2022', 'ACF0903', 'CÁLCULO VECTORIAL', 5, '75', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2022', 'AED1286', 'ESTRUCTURA DE DATOS', 5, '81', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2022', 'SCC1013', 'INVESTIGACIÓN DE OPERACIONES', 4, '86', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2022', 'SCD1008', 'FUND. DE BASES DE DATOS', 5, '88', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2022', 'AEC1061', 'SISTEMAS OPERATIVOS', 4, '82', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2022', 'SCC1019', 'PRINCIPIOS ELÉCTRICOS', 4, '82', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2022', 'AEC1053', 'PROB. Y ESTADÍSTICA', 4, '81', 'Ordinaria'],

        // SEMESTRE 4
        ['ENERO-JUNIO/2023', 'ACF0905', 'ECUACIONES DIFERENCIALES', 5, '74', 'Ordinaria'],
        ['ENERO-JUNIO/2023', 'SCC1017', 'MÉTODOS NUMÉRICOS', 4, '78', 'Ordinaria'],
        ['ENERO-JUNIO/2023', 'SCD1027', 'TOPICOS AVANZADOS DE PROG.', 5, '95', 'Ordinaria'],
        ['ENERO-JUNIO/2023', 'SCA1025', 'TALLER DE BASES DE DATOS', 4, '91', 'Ordinaria'],
        ['ENERO-JUNIO/2023', 'SCD1022', 'SIMULACIÓN', 5, '88', 'Ordinaria'],
        ['ENERO-JUNIO/2023', 'SCD1004', 'ARQUITECTURA DE COMPUTADORAS', 5, '82', 'Ordinaria'],
        ['ENERO-JUNIO/2023', 'AEC1046', 'FUND. DE TELECOMUNICACIONES', 4, '84', 'Ordinaria'],

        // SEMESTRE 5
        ['AGOSTO-DICIEMBRE/2023', 'AEW-2201', 'PROGRAMACIÓN WEB', 5, '100', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2023', 'SCD1021', 'REDES DE COMPUTADORAS', 5, '83', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2023', 'AEC1034', 'FUND. DE ING. DE SOFTWARE', 4, '87', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2023', 'SCA1002', 'ADMIN. DE BASES DE DATOS', 4, '87', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2023', 'SCD1015', 'LENGUAJES Y AUTÓMATAS I', 5, '70', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2023', 'SCC1005', 'CULTURA EMPRESARIAL', 4, '96', 'Ordinaria'],

        // SEMESTRE 6
        ['ENERO-JUNIO/2024', 'SCD1016', 'LENGUAJES Y AUTÓMATAS II', 5, '85', 'Ordinaria'],
        ['ENERO-JUNIO/2024', 'SCA1026', 'CONMUT. Y ENRUT. EN REDES', 4, '84', 'Ordinaria'],
        ['ENERO-JUNIO/2024', 'SCD1011', 'INGENIERÍA DE SOFTWARE', 5, '90', 'Ordinaria'],
        ['ENERO-JUNIO/2024', 'SCD1025', 'TALLER DE SIST. OPERATIVOS', 5, '92', 'Ordinaria'],
        ['ENERO-JUNIO/2024', 'SCC1010', 'GRAFICACIÓN', 4, '88', 'Ordinaria'],

        // SEMESTRE 7
        ['AGOSTO-DICIEMBRE/2024', 'ACA0909', 'TALLER DE INVESTIGACIÓN I', 4, '95', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2024', 'SCD1003', 'ADMINISTRACIÓN DE REDES', 5, '100', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2024', 'SCC1014', 'LENGUAJES DE INTERFAZ', 4, '97', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2024', 'SCD1024', 'SISTEMAS PROGRAMABLES', 5, '91', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2024', 'SCC1011', 'GESTIÓN DE PROYECTOS DE SOFT.', 4, '89', 'Ordinaria'],
        ['AGOSTO-DICIEMBRE/2024', 'SCC1006', 'PROG. LÓGICA Y FUNCIONAL', 4, '100', 'Ordinaria'],

        // SEMESTRE 8 (Cursando)
        ['ENERO-JUNIO/2025', 'ACA0910', 'TALLER DE INVESTIGACIÓN II', 4, 'AC', 'Cursando'],
        ['ENERO-JUNIO/2025', 'SCD1012', 'INTELIGENCIA ARTIFICIAL', 5, 'AC', 'Cursando'],
        ['ENERO-JUNIO/2025', 'AEW-2202', 'PROG. DISP. MOV. IOS', 5, 'AC', 'Cursando'],
        ['ENERO-JUNIO/2025', 'AEW-2204', 'PROG. DISP. MOV. ANDROID', 5, 'AC', 'Cursando'],
        ['ENERO-JUNIO/2025', 'AEW-2205', 'DESARROLLO WEB AVANZADO', 5, 'AC', 'Cursando'],
        ['ENERO-JUNIO/2025', 'RMD-2201', 'E-BUSINESS', 4, 'AC', 'Cursando']
    ];

    $stmt = $pdo->prepare("INSERT INTO calificaciones (user_id, periodo, clave, materia, creditos, calificacion, evaluacion) VALUES (?, ?, ?, ?, ?, ?, ?)");
    foreach ($materias as $m) {
        $stmt->execute([$userId, $m[0], $m[1], $m[2], $m[3], $m[4], $m[5]]);
    }

    echo "<h1>✅ Kardex Actualizado desde Retícula</h1>";
    echo "<p>Alumno: JULIO ALEJANDRO MEDINA CERVANTES</p>";
    echo "<p>Se han cargado todos los semestres según tu Avance Curricular.</p>";
} catch (Exception $e) {
    echo "<h1>❌ Error:</h1> " . $e->getMessage();
}
