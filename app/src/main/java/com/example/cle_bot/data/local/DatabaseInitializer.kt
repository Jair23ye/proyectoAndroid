package com.example.cle_bot.data.local

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {

    fun populateDatabase(database: CleBotDatabase) {
        val tramiteDao = database.tramiteDao()
        val userDao = database.userDao()
        val kardexDao = database.kardexDao()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. POBLAR TRÁMITES
                val tramites = listOf(
                    TramiteEntity(1, "Inscripción", "Proceso inicial para alumnos de nuevo ingreso al ITTG.", "Administrativo"),
                    TramiteEntity(2, "Reinscripción", "Proceso semestral obligatorio para alumnos regulares en el portal SII.", "Académico"),
                    TramiteEntity(3, "Constancia de Estudios", "Documento oficial de inscripción vigente. Costo: $150 MXN.", "Administrativo"),
                    TramiteEntity(4, "Baja Temporal", "Pausa oficial de estudios por un semestre.", "Administrativo"),
                    TramiteEntity(5, "Carrera: Sistemas Computacionales", "Perfil: Desarrollo de software, IA y Redes. Especialidad: Tecnologías Web y Móvil.", "Información")
                )
                tramiteDao.insertTramites(tramites)

                // 2. POBLAR USUARIO
                val userId = 1
                val existingUser = userDao.getUserById(userId)
                if (existingUser == null) {
                    userDao.register(
                        UserEntity(
                            id = userId,
                            name = "JULIO ALEJANDRO MEDINA CERVANTES",
                            controlNumber = "21270156",
                            email = "L21270156@tuxtla.tecnm.mx",
                            password = "MedCer2214$"
                        )
                    )
                }

                // 3. LIMPIAR E INSERTAR KARDEX REAL CON MATERIAS REPROBADAS (N/A)
                kardexDao.deleteForUser(userId)
                val fullKardex = listOf(
                    // 2021
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2021", clave = "ACF0901", materia = "CALC.DIFER.", creditos = 5, calificacion = "70", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2021", clave = "AED1285", materia = "FUND. DE PROG.", creditos = 5, calificacion = "79", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2021", clave = "ACA0907", materia = "TALL.DE ETICA", creditos = 4, calificacion = "94", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2021", clave = "AEF1041", materia = "MAT.DISCRETAS", creditos = 5, calificacion = "70", evaluacion = "Complementaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2021", clave = "SCH1024", materia = "TALL.ADMON.", creditos = 4, calificacion = "70", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2021", clave = "ACC0906", materia = "FUND.INVEST.", creditos = 4, calificacion = "94", evaluacion = "Ordinaria"),
                    
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2021", clave = "ACF0902", materia = "CALC.INTEGRAL", creditos = 5, calificacion = "87", evaluacion = "Complementaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2021", clave = "AEC1008", materia = "CONTAB.FINANC.", creditos = 4, calificacion = "93", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2021", clave = "AEC1058", materia = "QUIMICA", creditos = 4, calificacion = "95", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2021", clave = "AED1286", materia = "PROG. ORIEN. A OBJ.", creditos = 5, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2021", clave = "ACF0903", materia = "ALG.LINEAL", creditos = 5, calificacion = "84", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2021", clave = "SCF1006", materia = "FISICA GRAL.", creditos = 5, calificacion = "70", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2021", clave = "ACD0908", materia = "DESARROLLO SUSTENTABLE", creditos = 5, calificacion = "100", evaluacion = "Ordinaria"),
                    
                    // 2022
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2022", clave = "AED1286", materia = "PROG. ORIEN. A OBJ.", creditos = 5, calificacion = "70", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2022", clave = "ACF0904", materia = "CALC.VECTORIAL", creditos = 5, calificacion = "80", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2022", clave = "SCC1013", materia = "INV.DE OPERAC.", creditos = 4, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2022", clave = "SCD1018", materia = "PRINC.ELEC.APLIC.DIG", creditos = 5, calificacion = "88", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2022", clave = "AEF1052", materia = "PROB.Y ESTAD.", creditos = 5, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2022", clave = "SCA1026", materia = "TALLER DE SIST.OPERA", creditos = 4, calificacion = "N/A", evaluacion = "Ordinaria"),
                    
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2022", clave = "AED1026", materia = "EST.DATOS", creditos = 5, calificacion = "70", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2022", clave = "SCC1013", materia = "INV.DE OPERAC.", creditos = 4, calificacion = "78", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2022", clave = "AEF1052", materia = "PROB.Y ESTAD.", creditos = 5, calificacion = "80", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2022", clave = "SCA1026", materia = "TALLER DE SIST.OPERA", creditos = 4, calificacion = "80", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2022", clave = "AEF1031", materia = "FUND.BASES.DATOS", creditos = 5, calificacion = "77", evaluacion = "Ordinaria"),
                    
                    // 2023
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2023", clave = "AEC1034", materia = "FUND. DE TELEC.", creditos = 4, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2023", clave = "ACF0905", materia = "ECUAC.DIFER.", creditos = 5, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2023", clave = "SCC1017", materia = "METOD.NUMERICOS", creditos = 4, calificacion = "75", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2023", clave = "SCD1027", materia = "TOP.AVANZ.DE PROG.", creditos = 5, calificacion = "90", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2023", clave = "SCA1025", materia = "TALLER.BASES.DATOS", creditos = 4, calificacion = "90", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2023", clave = "SCD1022", materia = "SIMULACION", creditos = 5, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2023", clave = "SCD1003", materia = "ARQ.DE COMPUTADORAS", creditos = 5, calificacion = "70", evaluacion = "Ordinaria"),
                    
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2023", clave = "SCB1001", materia = "ADMON.BASE D.", creditos = 5, calificacion = "70", evaluacion = "Complementaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2023", clave = "ACF0905", materia = "ECUAC.DIFER.", creditos = 5, calificacion = "N/A", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2023", clave = "AEC1034", materia = "FUND. DE TELEC.", creditos = 4, calificacion = "73", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2023", clave = "SCD1022", materia = "SIMULACION", creditos = 5, calificacion = "76", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2023", clave = "AEC1061", materia = "SIST.OPERATIVOS", creditos = 4, calificacion = "80", evaluacion = "Ordinaria"),
                    
                    // 2024
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2024", clave = "ACF0905", materia = "ECUAC.DIFER.", creditos = 5, calificacion = "81", evaluacion = "Curso Especial"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2024", clave = "AEB1055", materia = "PROG. WEB", creditos = 5, calificacion = "90", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2024", clave = "SCD1021", materia = "REDES DE COMP.", creditos = 5, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2024", clave = "SCC1007", materia = "FUND. DE. ING. SOFT.", creditos = 4, calificacion = "N/A", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2024", clave = "SCD1015", materia = "LENG.Y AUTOM.I", creditos = 5, calificacion = "N/A", evaluacion = "Ordinaria"),
                    
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2024", clave = "SCD1015", materia = "LENG.Y AUTOM.I", creditos = 5, calificacion = "70", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2024", clave = "SCD1021", materia = "REDES DE COMP.", creditos = 5, calificacion = "72", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2024", clave = "SCC1007", materia = "FUND. DE. ING. SOFT.", creditos = 4, calificacion = "83", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2024", clave = "SCC1005", materia = "CULT.EMPRES.", creditos = 4, calificacion = "83", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2024", clave = "SCC1014", materia = "LENGUAJES DE INTERFAZ", creditos = 4, calificacion = "N/A", evaluacion = "Ordinaria"),
                    
                    // 2025
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2025", clave = "SCC1014", materia = "LENGUAJES DE INTERFAZ", creditos = 4, calificacion = "97", evaluacion = "Repeticion Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2025", clave = "SCD1016", materia = "LENGUAJES Y AUT, II", creditos = 5, calificacion = "88", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2025", clave = "SCD1004", materia = "CONM.ENRUT.RED.DATOS", creditos = 5, calificacion = "70", evaluacion = "Complementaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2025", clave = "SCD1011", materia = "ING.DE SOFTWARE", creditos = 5, calificacion = "70", evaluacion = "Complementaria"),
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2025", clave = "SCC1010", materia = "GRAFICACION", creditos = 4, calificacion = "84", evaluacion = "Ordinaria"),
                    
                    CalificacionEntity(userId = userId, periodo = "VERANO/2025", clave = "SCC1023", materia = "SISTEMAS PROGR.", creditos = 4, calificacion = "100", evaluacion = "Ordinaria"),
                    
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2025", clave = "ACA0909", materia = "TALL.INVEST.I", creditos = 4, calificacion = "85", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2025", clave = "SCA1002", materia = "ADMON.DE REDES", creditos = 4, calificacion = "95", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2025", clave = "SCC-1019", materia = "PROG. LOG. Y FUNCIONAL", creditos = 4, calificacion = "83", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2025", clave = "SCG1009", materia = "GEST. PROY.SOFT.", creditos = 6, calificacion = "79", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2025", clave = "SCC1012", materia = "INTELIGENCIA ARTIFICIAL", creditos = 4, calificacion = "100", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2025", clave = "WMD-2301", materia = "PRO. PROY. Y PLNS. TECNOLOGICOS", creditos = 5, calificacion = "82", evaluacion = "Ordinaria"),
                    CalificacionEntity(userId = userId, periodo = "AGOSTO-DICIEMBRE/2025", clave = "WMD-2302", materia = "E-BUSINESS", creditos = 5, calificacion = "N/A", evaluacion = "Ordinaria"),
                    
                    // 2026
                    CalificacionEntity(userId = userId, periodo = "ENERO-JUNIO/2026", clave = "50", materia = "ACTIVIDADES COMPLEMENTARIAS", creditos = 5, calificacion = "100", evaluacion = "Ordinaria")
                )
                kardexDao.insertAll(fullKardex)
            } catch (e: Exception) {
                Log.e("DatabaseInitializer", "Error populating database", e)
            }
        }
    }
}
