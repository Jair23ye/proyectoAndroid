<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(['success' => false, 'message' => 'Metodo no permitido.'], 405);
}

$input = readJsonInput();

$name = value($input, 'name');
$controlNumber = value($input, 'controlNumber');
$email = strtolower(value($input, 'email'));
$password = (string)($input['password'] ?? '');

validateRequired([
    'nombre' => $name,
    'correo' => $email,
    'contraseña' => $password,
]);
validateEmailAddress($email);

if (strlen($password) < 6) {
    respond([
        'success' => false,
        'message' => 'La contraseña debe tener al menos 6 caracteres.',
    ], 422);
}

$existing = $pdo->prepare('SELECT id FROM users WHERE email = ? LIMIT 1');
$existing->execute([$email]);
if ($existing->fetch()) {
    respond([
        'success' => false,
        'message' => 'Ya existe una cuenta con ese correo.',
    ], 409);
}

$passwordHash = password_hash($password, PASSWORD_DEFAULT);
$role = str_ends_with($email, '@tuxtla.tecnm.mx') ? 'student' : 'external';

$statement = $pdo->prepare(
    'INSERT INTO users (name, control_number, email, password_hash, role)
     VALUES (?, ?, ?, ?, ?)'
);
$statement->execute([
    $name,
    $controlNumber !== '' ? $controlNumber : null,
    $email,
    $passwordHash,
    $role,
]);

respond([
    'success' => true,
    'message' => 'Cuenta creada correctamente. Ahora puedes iniciar sesion.',
    'userId' => (int)$pdo->lastInsertId(),
], 201);
