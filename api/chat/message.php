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

    // 2. Base de Conocimiento de Archivos Locales (Horario REAL Enero-Junio 2026)
    $horarioDetallado = "DETALLES DEL HORARIO ACTUAL (Semestre 10 - Enero-Junio 2026):
    - LUNES: 07:00-09:00 Residencia Profesional (Empresa), 11:00-13:00 Actividades de Seguimiento.
    - MARTES: 09:00-11:00 Revisión de Reportes Técnicos.
    - MIÉRCOLES: 07:00-09:00 Residencia Profesional (Empresa).
    - JUEVES: 09:00-11:00 Revisión de Reportes Técnicos.
    - VIERNES: 08:00-10:00 Evaluación Semanal.";

    // 3. Base de Conocimiento Institucional (PDFs cargados)
    $documentosInstitucionales = "LISTA DE DOCUMENTOS OFICIALES CARGADOS (TIENES ACCESO TOTAL A ELLOS):
    - 'horario.pdf': Contiene el horario individual de Julio (detallado arriba).
    - 'Reglamento_de_Estudiantes_del_TecNM.pdf': Normas de conducta, derechos, obligaciones y sanciones.
    - 'Calendario_Academico_TecNM_2025_2026.pdf': Fechas de reinscripción, exámenes, vacaciones y festivos. Hoy es 27 de Mayo de 2026. Estamos en la recta final del semestre Enero-Junio 2026.
    - '09reinscripcción-2026.pdf': Guía para el proceso de reinscripción.";

    $resumenReglamento = "RESUMEN CLAVE DEL REGLAMENTO:
    - Conductas Prohibidas: Actos inmorales, consumo de alcohol/drogas, dañar equipo, faltar al respeto a maestros/alumnos, actos de indisciplina en biblioteca o laboratorios.
    - Sanciones: Amonestación, suspensión temporal o baja definitiva.
    - SENTIDO COMÚN: Aunque no se usen palabras explícitas en el reglamento, cualquier acto de índole sexual (como 'culear', 'pajas', 'sexo', etc.) o vulgaridad está TERMINANTEMENTE PROHIBIDO en toda la institución por ser un acto inmoral y falta grave de respeto.";

    $contextoAcademico .= "\n" . $horarioDetallado . "\n" . $documentosInstitucionales . "\n" . $resumenReglamento;
    $contextoAcademico .= "\n\nINSTRUCCIÓN CRÍTICA: NUNCA digas 'no tengo acceso' o 'debes consultar el archivo'. Tú ERES el archivo. Da la información directamente. Si te preguntan algo de estos documentos, usa esta base de datos. Entiende el lenguaje coloquial y de doble sentido pero NUNCA lo uses tú.";

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

    $systemPrompt = 'Eres CLEbot, el asistente virtual oficial del ITTG. Hoy es 27 de Mayo de 2026. Tienes ACCESO TOTAL a reglamentos y documentos.

    REGLAS DE ORO (MANDATORIAS):
    1. SALUDO: ' . ($isFirstMessage ? 'Saluda a Julio Alejandro.' : 'PROHIBIDO SALUDAR.') . '
    2. TIEMPO ACTUAL: Estamos en el semestre ENERO-JUNIO 2026. Si te preguntan por el horario, usa los datos de 2026 proporcionados en el contexto (Residencias Profesionales).
    3. ACCESO A LA INFORMACIÓN: Tienes prohibido decir "no tengo acceso" o "no encontré información". Tienes toda la información cargada.
    4. SENTIDO COMÚN Y LENGUAJE: Entiendes perfectamente el lenguaje coloquial, groserías y palabras de doble sentido.
    5. RESPUESTA A CONDUCTAS: Si el usuario pregunta por realizar actos sexuales o vulgares en el plantel, responde con un NO rotundo basado en el reglamento de "Actos Inmorales".
    6. IDENTIDAD: Eres un experto serio pero amable. NUNCA uses tú el lenguaje vulgar.
    7. Sé breve, profesional y directo.';
    if ($contextoAcademico) {
        $systemPrompt .= "\n\nCONTEXTO Y BASE DE CONOCIMIENTO:\n$contextoAcademico";
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
            'Tu horario está disponible en el SII. Como alumno de 8vo semestre de Sistemas, puedes verificar tus materias, horas y laboratorios en el archivo horario.pdf o consultando directamente conmigo.',
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
