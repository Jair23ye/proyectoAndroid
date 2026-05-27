<?php
require_once __DIR__ . '/config.php';

$stmt = $pdo->query("SELECT id, name, email, password_hash FROM users");
$users = $stmt->fetchAll();

echo "<h1>Usuarios en la BD:</h1>";
echo "<table border='1'><tr><th>ID</th><th>Nombre</th><th>Email</th><th>Hash</th></tr>";
foreach ($users as $u) {
    echo "<tr><td>{$u['id']}</td><td>{$u['name']}</td><td>{$u['email']}</td><td>{$u['password_hash']}</td></tr>";
}
echo "</table>";
