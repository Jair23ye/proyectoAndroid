package com.example.cle_bot.navigation

object NavRoutes {
    const val LOGIN           = "login"
    const val REGISTER        = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD  = "reset_password"
    const val SUPPORT         = "support"
    const val FAQ             = "faq"
    const val CHAT            = "chat"
    const val TRAMITES        = "tramites"
    const val TRAMITE_DETAIL  = "tramite_detail/{tramiteId}"
    const val KARDEX          = "kardex"
    
    fun createTramiteDetailRoute(tramiteId: Int): String {
        return "tramite_detail/$tramiteId"
    }
}
