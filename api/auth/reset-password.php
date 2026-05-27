<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(['success' => false, 'message' => 'Metodo no permitido.'], 405);
}

$input = readJsonInput();
$token = value($input, 'token');
$newPassword = (string)($input['newPassword'] ?? '');

validateRequired([
    'token' => $token,
    'nueva contraseña' => $newPassword,
]);

if (strlen($newPassword) < 6) {
    respond([
        'success' => false,
        'message' => 'La contraseña debe tener al menos 6 caracteres.',
    ], 422);
}

$tokenHash = hash('sha256', $token);
$statement = $pdo->prepare(
    'SELECT id, user_id
     FROM password_resets
     WHERE token_hash = ?
       AND used_at IS NULL
       AND expires_at > NOW()
     ORDER BY id DESC
     LIMIT 1'
);
$statement->execute([$tokenHash]);
$reset = $statement->fetch();

if (!$reset) {
    respond([
        'success' => false,
        'message' => 'El token no existe o ya expiro.',
    ], 422);
}

$pdo->beginTransaction();

$updateUser = $pdo->prepare('UPDATE users SET password_hash = ? WHERE id = ?');
$updateUser->execute([password_hash($newPassword, PASSWORD_DEFAULT), (int)$reset['user_id']]);

$updateReset = $pdo->prepare('UPDATE password_resets SET used_at = NOW() WHERE id = ?');
$updateReset->execute([(int)$reset['id']]);

$pdo->commit();

respond([
    'success' => true,
    'message' => 'Contraseña actualizada correctamente.',
]);
