package com.example.cle_bot.data

import com.example.cle_bot.data.local.*

class TramiteRepository(
    private val dao: TramiteDao
) {
    suspend fun getTramites(search: String? = null, category: String? = null): List<TramiteEntity> {
        return fetchLocalTramites(search)
    }

    private suspend fun fetchLocalTramites(search: String?): List<TramiteEntity> {
        return if (search.isNullOrBlank()) {
            dao.getAllTramites()
        } else {
            dao.searchTramites(search)
        }
    }

    suspend fun getTramiteDetail(id: Int): TramiteDetailResult {
        return fetchLocalTramiteDetail(id)
    }

    private suspend fun fetchLocalTramiteDetail(id: Int): TramiteDetailResult {
        val tramite = dao.getTramiteById(id) ?: throw Exception("Trámite no encontrado localmente")
        val requisitos = dao.getRequisitosByTramiteId(id)
        val pasos = dao.getPasosByTramiteId(id)
        return TramiteDetailResult(tramite, requisitos, pasos)
    }

    suspend fun getProgreso(userId: Int, tramiteId: Int): List<ProgresoEntity> {
        return dao.getProgreso(userId, tramiteId)
    }

    suspend fun saveProgreso(userId: Int, tramiteId: Int, pasoId: Int, isCompleted: Boolean) {
        val entity = ProgresoEntity(userId, tramiteId, pasoId, isCompleted, isSynced = true)
        dao.insertProgreso(entity)
    }
}

data class TramiteDetailResult(
    val tramite: TramiteEntity,
    val requisitos: List<RequisitoEntity>,
    val pasos: List<PasoEntity>
)
