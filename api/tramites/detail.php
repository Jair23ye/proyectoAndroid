<?php
require_once '../config.php';

if (!isset($_GET['id']) || !is_numeric($_GET['id'])) {
    respond(['success' => false, 'message' => 'ID de trámite no proporcionado o inválido.'], 400);
}

$id = (int)$_GET['id'];

try {
    // Obtener detalles del trámite
    $stmt = $pdo->prepare("SELECT * FROM tramites WHERE id = :id");
    $stmt->execute([':id' => $id]);
    $tramite = $stmt->fetch();
    
    if (!$tramite) {
        respond(['success' => false, 'message' => 'Trámite no encontrado.'], 404);
    }
    
    // Obtener requisitos
    $stmt = $pdo->prepare("SELECT * FROM requisitos WHERE tramite_id = :id ORDER BY id ASC");
    $stmt->execute([':id' => $id]);
    $tramite['requisitos'] = $stmt->fetchAll();
    
    // Obtener pasos
    $stmt = $pdo->prepare("SELECT * FROM pasos WHERE tramite_id = :id ORDER BY step_number ASC");
    $stmt->execute([':id' => $id]);
    $tramite['pasos'] = $stmt->fetchAll();
    
    respond([
        'success' => true,
        'data' => $tramite
    ]);
} catch (PDOException $e) {
    respond([
        'success' => false,
        'message' => 'Error al obtener el trámite: ' . $e->getMessage()
    ], 500);
}
