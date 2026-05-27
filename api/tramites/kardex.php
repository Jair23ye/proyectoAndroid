<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    respond(['success' => false, 'message' => 'Método no permitido.'], 405);
}

$userId = isset($_GET['user_id']) ? (int)$_GET['user_id'] : 0;

if ($userId <= 0) {
    respond([
        'success' => false,
        'message' => 'ID de usuario no proporcionado o inválido.',
    ], 400);
}

try {
    $statement = $pdo->prepare(
        'SELECT periodo, clave, materia, creditos, calificacion, evaluacion, observaciones
         FROM calificaciones
         WHERE user_id = ?
         ORDER BY id ASC'
    );
    $statement->execute([$userId]);
    $records = $statement->fetchAll();

    $promedioGeneral = 0;
    $totalCreditos = 0;
    $creditosAprobados = 0;
    $sumCalificaciones = 0;
    $countMateriasConNota = 0;

    foreach ($records as $r) {
        $totalCreditos += (int)$r['creditos'];
        if (is_numeric($r['calificacion'])) {
            $nota = (float)$r['calificacion'];
            $sumCalificaciones += $nota;
            $countMateriasConNota++;
            if ($nota >= 70) {
                $creditosAprobados += (int)$r['creditos'];
            }
        }
    }

    if ($countMateriasConNota > 0) {
        $promedioGeneral = round($sumCalificaciones / $countMateriasConNota, 2);
    }

    respond([
        'success' => true,
        'data' => $records,
        'promedioGeneral' => $promedioGeneral,
        'creditosTotales' => $totalCreditos,
        'creditosAprobados' => $creditosAprobados
    ]);
} catch (PDOException $e) {
    respond([
        'success' => false,
        'message' => 'Error al obtener el Kardex: ' . $e->getMessage(),
    ], 500);
}
