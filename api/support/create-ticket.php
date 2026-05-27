<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(['success' => false, 'message' => 'Metodo no permitido.'], 405);
}

$input = readJsonInput();

$userId = isset($input['userId']) && $input['userId'] !== null ? (int)$input['userId'] : null;
$name = value($input, 'name');
$email = strtolower(value($input, 'email'));
$category = value($input, 'category');
$description = value($input, 'description');

validateRequired([
    'nombre' => $name,
    'correo' => $email,
    'categoria' => $category,
    'descripcion' => $description,
]);
validateEmailAddress($email);

$statement = $pdo->prepare(
    'INSERT INTO support_tickets (user_id, name, email, category, description)
     VALUES (?, ?, ?, ?, ?)'
);
$statement->execute([$userId, $name, $email, $category, $description]);

respond([
    'success' => true,
    'message' => 'Solicitud enviada correctamente. Soporte revisara tu caso.',
    'ticketId' => (int)$pdo->lastInsertId(),
], 201);
