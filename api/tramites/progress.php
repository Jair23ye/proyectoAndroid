<?php
require_once '../config.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (!isset($_GET['user_id']) || !isset($_GET['tramite_id'])) {
        respond(['success' => false, 'message' => 'Faltan parámetros.'], 400);
    }
    
    $user_id = (int)$_GET['user_id'];
    $tramite_id = (int)$_GET['tramite_id'];
    
    try {
        $stmt = $pdo->prepare("SELECT paso_id, is_completed FROM progreso_usuario WHERE user_id = :user_id AND tramite_id = :tramite_id");
        $stmt->execute([':user_id' => $user_id, ':tramite_id' => $tramite_id]);
        $progreso = $stmt->fetchAll();
        
        respond([
            'success' => true,
            'data' => $progreso
        ]);
    } catch (PDOException $e) {
        respond(['success' => false, 'message' => 'Error: ' . $e->getMessage()], 500);
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = readJsonInput();
    validateRequired(['user_id' => value($data, 'user_id'), 'tramite_id' => value($data, 'tramite_id'), 'paso_id' => value($data, 'paso_id'), 'is_completed' => value($data, 'is_completed')]);
    
    $user_id = (int)$data['user_id'];
    $tramite_id = (int)$data['tramite_id'];
    $paso_id = (int)$data['paso_id'];
    $is_completed = (int)$data['is_completed'];
    
    try {
        $stmt = $pdo->prepare("
            INSERT INTO progreso_usuario (user_id, tramite_id, paso_id, is_completed)
            VALUES (:user_id, :tramite_id, :paso_id, :is_completed)
            ON DUPLICATE KEY UPDATE is_completed = :is_completed, updated_at = CURRENT_TIMESTAMP
        ");
        $stmt->execute([
            ':user_id' => $user_id,
            ':tramite_id' => $tramite_id,
            ':paso_id' => $paso_id,
            ':is_completed' => $is_completed
        ]);
        
        respond([
            'success' => true,
            'message' => 'Progreso guardado.'
        ]);
    } catch (PDOException $e) {
        respond(['success' => false, 'message' => 'Error: ' . $e->getMessage()], 500);
    }
}
