package com.example.cle_bot.data

import com.example.cle_bot.data.local.*

class TramiteRepository(
    private val api: CleBotApi = ApiClient.api,
    private val dao: TramiteDao
) {
    suspend fun getTramites(search: String? = null, category: String? = null): List<TramiteEntity> {
        return try {
            val response = api.getTramites(search, category)
            if (response.success && response.data != null) {
                val entities = response.data.map {
                    TramiteEntity(it.id, it.title, it.description, it.category)
                }
                dao.insertTramites(entities)
            }
            fetchLocalTramites(search)
        } catch (e: Exception) {
            fetchLocalTramites(search)
        }
    }

    private suspend fun fetchLocalTramites(search: String?): List<TramiteEntity> {
        return if (search.isNullOrBlank()) {
            dao.getAllTramites()
        } else {
            dao.searchTramites(search)
        }
    }

    suspend fun getTramiteDetail(id: Int): TramiteDetailResult {
        return try {
            val response = api.getTramiteDetail(id)
            if (response.success && response.data != null) {
                val dto = response.data
                val tramite = TramiteEntity(dto.id, dto.title, dto.description, dto.category)
                dao.insertTramites(listOf(tramite))
                
                dto.requisitos?.let { reqs ->
                    dao.insertRequisitos(reqs.map { RequisitoEntity(it.id, it.tramite_id, it.description, it.is_mandatory == 1) })
                }
                
                dto.pasos?.let { pasos ->
                    dao.insertPasos(pasos.map { PasoEntity(it.id, it.tramite_id, it.step_number, it.title, it.description) })
                }
            }
            fetchLocalTramiteDetail(id)
        } catch (e: Exception) {
            fetchLocalTramiteDetail(id)
        }
    }

    private suspend fun fetchLocalTramiteDetail(id: Int): TramiteDetailResult {
        val tramite = dao.getTramiteById(id) ?: throw Exception("Trámite no encontrado localmente")
        val requisitos = dao.getRequisitosByTramiteId(id)
        val pasos = dao.getPasosByTramiteId(id)
        return TramiteDetailResult(tramite, requisitos, pasos)
    }

    suspend fun getProgreso(userId: Int, tramiteId: Int): List<ProgresoEntity> {
        try {
            val response = api.getProgreso(userId, tramiteId)
            if (response.success && response.data != null) {
                val entities = response.data.map {
                    ProgresoEntity(userId, tramiteId, it.paso_id, it.is_completed == 1, isSynced = true)
                }
                dao.insertProgresos(entities)
            }
        } catch (e: Exception) {
            // Ignore online error, return local
        }
        return dao.getProgreso(userId, tramiteId)
    }

    suspend fun saveProgreso(userId: Int, tramiteId: Int, pasoId: Int, isCompleted: Boolean) {
        val entity = ProgresoEntity(userId, tramiteId, pasoId, isCompleted, isSynced = false)
        dao.insertProgreso(entity)
        try {
            val response = api.saveProgreso(SaveProgresoRequest(userId, tramiteId, pasoId, if (isCompleted) 1 else 0))
            if (response.success) {
                dao.markAsSynced(userId, tramiteId, pasoId)
            }
        } catch (e: Exception) {
            // Will sync later
        }
    }
}

data class TramiteDetailResult(
    val tramite: TramiteEntity,
    val requisitos: List<RequisitoEntity>,
    val pasos: List<PasoEntity>
)
