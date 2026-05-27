<?php
declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Content-Type');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

$dbHost = '127.0.0.1';
$dbName = 'clebot_db';
$dbUser = 'root';
$dbPass = '';

try {
    $pdo = new PDO(
        "mysql:host={$dbHost};dbname={$dbName};charset=utf8mb4",
        $dbUser,
        $dbPass,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        ]
    );
} catch (PDOException $exception) {
    respond([
        'success' => false,
        'message' => 'No se pudo conectar con la base de datos.',
    ], 500);
}

function readJsonInput(): array
{
    $rawInput = file_get_contents('php://input');
    if ($rawInput === false || trim($rawInput) === '') {
        return [];
    }

    $data = json_decode($rawInput, true);
    if (!is_array($data)) {
        respond([
            'success' => false,
            'message' => 'El cuerpo de la solicitud no es JSON valido.',
        ], 400);
    }

    return $data;
}

function respond(array $payload, int $status = 200): void
{
    http_response_code($status);
    echo json_encode($payload, JSON_UNESCAPED_UNICODE);
    exit;
}

function value(array $data, string $key): string
{
    return trim((string)($data[$key] ?? ''));
}

function validateRequired(array $fields): void
{
    foreach ($fields as $name => $value) {
        if (trim((string)$value) === '') {
            respond([
                'success' => false,
                'message' => "El campo {$name} es obligatorio.",
            ], 422);
        }
    }
}

function validateEmailAddress(string $email): void
{
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        respond([
            'success' => false,
            'message' => 'Ingresa un correo valido.',
        ], 422);
    }
}
