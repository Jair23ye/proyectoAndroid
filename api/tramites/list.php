<?php
require_once '../config.php';

try {
    $search = isset($_GET['search']) ? trim($_GET['search']) : '';
    $category = isset($_GET['category']) ? trim($_GET['category']) : '';
    
    $sql = "SELECT * FROM tramites WHERE 1=1";
    $params = [];
    
    if ($search !== '') {
        $sql .= " AND title LIKE :search";
        $params[':search'] = "%{$search}%";
    }
    
    if ($category !== '') {
        $sql .= " AND category = :category";
        $params[':category'] = $category;
    }
    
    $sql .= " ORDER BY title ASC";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $tramites = $stmt->fetchAll();
    
    respond([
        'success' => true,
        'data' => $tramites
    ]);
} catch (PDOException $e) {
    respond([
        'success' => false,
        'message' => 'Error al obtener trámites: ' . $e->getMessage()
    ], 500);
}
