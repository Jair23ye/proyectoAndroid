<?php
declare(strict_types=1);

require_once __DIR__ . '/../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(['success' => false, 'message' => 'Metodo no permitido.'], 405);
}

$input = readJsonInput();
$userId = isset($input['userId']) && $input['userId'] !== null ? (int)$input['userId'] : null;
$message = value($input, 'message');

validateRequired(['mensaje' => $message]);

// Verificar si es el primer mensaje del usuario para el saludo
$isFirstMessage = true;
if ($userId) {
    $stmtCount = $pdo->prepare("SELECT COUNT(*) FROM chat_messages WHERE user_id = ? AND sender = 'user'");
    $stmtCount->execute([$userId]);
    $isFirstMessage = ($stmtCount->fetchColumn() == 0);
}

// Obtener contexto académico del usuario para la IA
$contextoAcademico = "";
if ($userId) {
    // 1. Promedio, Créditos y Datos de Carrera
    $stmt = $pdo->prepare("SELECT
        AVG(CASE WHEN calificacion REGEXP '^[0-9]+$' THEN CAST(calificacion AS UNSIGNED) END) as promedio,
        SUM(CASE WHEN calificacion >= 70 OR calificacion = 'AC' THEN creditos ELSE 0 END) as creditos_totales
        FROM calificaciones WHERE user_id = ?");
    $stmt->execute([$userId]);
    $res = $stmt->fetch();

    // 1.1 Obtener materias recientes (Últimos periodos) para evitar alucinaciones en el horario
    $stmtMaterias = $pdo->prepare("SELECT materia, periodo, calificacion FROM calificaciones WHERE user_id = ? ORDER BY periodo DESC, id DESC LIMIT 15");
    $stmtMaterias->execute([$userId]);
    $materiasRecientes = $stmtMaterias->fetchAll(PDO::FETCH_ASSOC);
    $listaMaterias = "";
    foreach ($materiasRecientes as $m) {
        $listaMaterias .= "- " . $m['materia'] . " (Periodo: " . $m['periodo'] . ", Calif: " . $m['calificacion'] . ")\n";
    }

    // Datos específicos del plan proporcionado por el usuario
    $planEstudios = "ISIC-2010-224 (Sistemas Computacionales)";
    $especialidad = "Tecnologías Web y Móvil Aplicadas al Comercio Electrónico";
    $porcentajeAvance = "83%";

    if ($res) {
        $promedio = round((float)$res['promedio'], 2);
        $creditos = $res['creditos_totales'];
        $contextoAcademico = "Alumno: JULIO ALEJANDRO MEDINA CERVANTES (ID: 21270156). Plan: $planEstudios. Especialidad: $especialidad. Promedio: $promedio. Créditos: $creditos. Avance: $porcentajeAvance.\n";
        $contextoAcademico .= "HISTORIAL RECIENTE / CARGA ACTUAL:\n" . $listaMaterias;
    }

    // 2. Base de Conocimiento de Archivos Locales (Extracción manual de horario.pdf)
    $horarioDetallado = "DETALLES DEL HORARIO (Extraído de horario.pdf):
    - LUNES: 07:00-09:00 Lenguajes de Interfaz (Aula L1), 09:00-11:00 Ing. de Software (Aula L2), 11:00-13:00 Conm. y Enrutamiento (Aula L3).
    - MARTES: 07:00-09:00 Lenguajes y Autómatas II (Aula L1), 09:00-11:00 Graficación (Aula L2), 11:00-13:00 Sistemas Programables (Aula L3).
    - MIÉRCOLES: 07:00-09:00 Lenguajes de Interfaz (Aula L1), 09:00-11:00 Ing. de Software (Aula L2), 11:00-13:00 Conm. y Enrutamiento (Aula L3).
    - JUEVES: 07:00-09:00 Lenguajes y Autómatas II (Aula L1), 09:00-11:00 Graficación (Aula L2), 11:00-13:00 Sistemas Programables (Aula L3).
    - VIERNES: 07:00-09:00 Actividades Complementarias, 09:00-11:00 Tutoría.";

    $contextoAcademico .= "\n" . $horarioDetallado;
    $contextoAcademico .= "\nINFORMACIÓN DE ARCHIVOS DISPONIBLES:
    - Reglamento: 'Reglamento_de_Estudiantes_del_TecNM.pdf'.
    - Calendario: 'Calendario_Academico_TecNM_2025_2026.pdf'.";

    // 3. Información de las carreras del Tec
    $stmt = $pdo->query("SELECT nombre FROM carreras");
    $carreras = $stmt->fetchAll(PDO::FETCH_COLUMN);
    if ($carreras) {
        $contextoAcademico .= " Carreras del ITTG: " . implode(", ", $carreras) . ".";
    }

    // 4. Materias de su retícula específica
    $stmt = $pdo->prepare("SELECT nombre, semestre FROM reticulas WHERE carrera_id = 1");
    $stmt->execute();
    $materias = $stmt->fetchAll(PDO::FETCH_ASSOC);
    if ($materias) {
        $nombres = [];
        foreach ($materias as $m) {
            $nombres[] = $m['nombre'] . " (Sem " . $m['semestre'] . ")";
        }
        $contextoAcademico .= " Retícula oficial: " . implode(", ", $nombres);
    }
}

$botReply = createBotReply($message, $contextoAcademico, $isFirstMessage);

$pdo->beginTransaction();

$insert = $pdo->prepare(
    'INSERT INTO chat_messages (user_id, sender, message)
     VALUES (?, ?, ?)'
);
$insert->execute([$userId, 'user', $message]);
$insert->execute([$userId, 'bot', $botReply]);

$pdo->commit();

respond([
    'success' => true,
    'message' => 'Respuesta generada.',
    'botReply' => $botReply,
]);

function createBotReply(string $message, string $contextoAcademico = "", bool $isFirstMessage = true): string
{
    $apiKey = 'sk-39f1a69d0ce84cdea5164c1932d45035'; // Reemplaza con tu llave real
    $apiUrl = 'https://api.deepseek.com/chat/completions';

    $systemPrompt = 'Eres CLEbot, el asistente virtual oficial del ITTG.

    REGLAS DE ORO:
    1. SALUDO: ' . ($isFirstMessage ? 'Saluda a Julio Alejandro.' : 'PROHIBIDO SALUDAR.') . '
    2. HORARIO: NUNCA mandes al usuario a leer el PDF. Tú tienes los datos. Si te preguntan el horario, RESPONDE con la tabla de horas, materias y aulas que viene en el contexto.
    3. DETALLES: Sé específico. Di: "El lunes tienes X materia a las Y hora en el aula Z".
    4. NO INVENTES: Usa estrictamente los datos del contexto.
    5. Sé breve y directo.';
    if ($contextoAcademico) {
        $systemPrompt .= " Contexto del alumno actual: $contextoAcademico";
    }

    $data = [
        'model' => 'deepseek-chat',
        'messages' => [
            ['role' => 'system', 'content' => $systemPrompt],
            ['role' => 'user', 'content' => $message]
        ],
        'stream' => false
    ];

    $ch = curl_init($apiUrl);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        'Content-Type: application/json',
        'Authorization: Bearer ' . $apiKey
    ]);

    $response = curl_exec($ch);
    $error = curl_error($ch);
    curl_close($ch);

    if ($error) {
        return "Error al conectar con la IA: " . $error;
    }

    $result = json_decode($response, true);
    if (isset($result['choices'][0]['message']['content'])) {
        return $result['choices'][0]['message']['content'];
    }

    // Fallback si falla la API
    return createBotFallbackReply($message);
}

function createBotFallbackReply(string $message): string
{
    $normalized = function_exists('mb_strtolower')
        ? mb_strtolower($message, 'UTF-8')
        : strtolower($message);

    $answers = [
        [
            ['constancia', 'constancias'],
            'Para solicitar una constancia, revisa el apartado de servicios escolares. Ten a la mano tu numero de control y verifica que no tengas adeudos.',
        ],
        [
            ['horario', 'horarios'],
            'Tu horario está disponible en el SII. Como alumno de 11vo semestre de Sistemas, puedes verificar tus materias y salones en el archivo horario.pdf que tenemos registrado.',
        ],
        [
            ['pago', 'pagos', 'adeudo', 'adeudos'],
            'Para pagos, confirma la referencia bancaria y conserva tu comprobante. Si el pago no se refleja, envia una solicitud a soporte con tu folio.',
        ],
        [
            ['libro', 'libros', 'biblioteca'],
            'Para temas de biblioteca, verifica si tienes prestamos pendientes y consulta los horarios de atencion antes de acudir.',
        ],
        [
            ['liberacion', 'servicio social', 'residencia', 'residencias'],
            'Para liberacion de servicio social o residencias, revisa que tus documentos esten completos y validados por el area correspondiente.',
        ],
        [
            ['reinscripcion', 'reinscripción', 'inscripcion', 'inscripción'],
            'Para reinscripcion, verifica fechas oficiales, adeudos, materias disponibles y autorizacion de tu tutor academico.',
        ],
    ];

    foreach ($answers as [$keywords, $reply]) {
        foreach ($keywords as $keyword) {
            if (str_contains($normalized, $keyword)) {
                return $reply;
            }
        }
    }

    return 'Lo siento, la IA de DeepSeek no respondió a tiempo. Puedo orientarte sobre constancias, horarios, pagos, biblioteca, liberacion y reinscripcion de forma básica.';
}
