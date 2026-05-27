<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(['success' => false, 'message' => 'Metodo no permitido.'], 405);
}

$input = readJsonInput();
$email = strtolower(value($input, 'email'));

validateRequired(['correo' => $email]);
validateEmailAddress($email);

$statement = $pdo->prepare('SELECT id FROM users WHERE email = ? LIMIT 1');
$statement->execute([$email]);
$user = $statement->fetch();

if (!$user) {
    respond([
        'success' => true,
        'message' => 'Si el correo existe, se generaran instrucciones para recuperar la contraseña.',
    ]);
}

$token = bin2hex(random_bytes(24));
$tokenHash = hash('sha256', $token);
$expiresAt = (new DateTimeImmutable('+30 minutes'))->format('Y-m-d H:i:s');

$insert = $pdo->prepare(
    'INSERT INTO password_resets (user_id, token_hash, expires_at)
     VALUES (?, ?, ?)'
);
$insert->execute([(int)$user['id'], $tokenHash, $expiresAt]);

respond([
    'success' => true,
    'message' => 'Token de recuperacion generado. En produccion este token se enviaria por correo.',
    'resetToken' => $token,
]);
