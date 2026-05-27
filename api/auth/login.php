<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(['success' => false, 'message' => 'Metodo no permitido.'], 405);
}

$input = readJsonInput();
$email = value($input, 'email'); // Sin strtolower para respetar la L mayúscula
$password = (string)($input['password'] ?? '');

validateRequired([
    'correo' => $email,
    'contraseña' => $password,
]);
validateEmailAddress($email);

$statement = $pdo->prepare(
    'SELECT id, name, control_number, email, password_hash, role, is_active
     FROM users
     WHERE email = ?
     LIMIT 1'
);
$statement->execute([$email]);
$user = $statement->fetch();

if (!$user || !password_verify($password, $user['password_hash'])) {
    respond([
        'success' => false,
        'message' => 'Correo o contraseña incorrectos.',
    ], 401);
}

if ((int)$user['is_active'] !== 1) {
    respond([
        'success' => false,
        'message' => 'La cuenta esta desactivada. Contacta a soporte.',
    ], 403);
}

respond([
    'success' => true,
    'message' => 'Inicio de sesion correcto.',
    'user' => [
        'id' => (int)$user['id'],
        'name' => $user['name'],
        'controlNumber' => $user['control_number'],
        'email' => $user['email'],
        'role' => $user['role'],
    ],
]);
