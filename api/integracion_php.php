<?php
require_once __DIR__ . '/config.php';

try {
    // 1. Limpiar usuario anterior si existe
    $pdo->prepare("DELETE FROM users WHERE email = ?")->execute(['L21270156@tuxtla.tecnm.mx']);

    // 2. Insertar tus datos reales
    $sql = "INSERT INTO users (name, control_number, email, password_hash, role)
            VALUES (?, ?, ?, ?, ?)";
    $stmt = $pdo->prepare($sql);
    $hash = password_hash('MedCer2214$', PASSWORD_BCRYPT, ['cost' => 12]);
    $stmt->execute([
        'JULIO ALEJANDRO MEDINA CERVANTES',
        '21270156',
        'L21270156@tuxtla.tecnm.mx',
        $hash,
        'admin'
    ]);

    echo "<h1>✅ Sincronización Exitosa</h1>";
    echo "<p>Usuario: JULIO ALEJANDRO MEDINA CERVANTES</p>";
    echo "<p>Correo: L21270156@tuxtla.tecnm.mx</p>";
    echo "<p>Contraseña: MedCer2214$</p>";
    echo "<p><b>Ya puedes intentar entrar desde el emulador.</b></p>";

} catch (Exception $e) {
    echo "<h1>❌ Error</h1>" . $e->getMessage();
}
