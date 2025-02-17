package com.github.masdaster.edma.models

import com.github.masdaster.edma.models.apis.EDSM.EDSMSystemResponse

data class SystemFinderResult(
    val name: String, val id: Long, val isPermitRequired: Boolean,
    val allegiance: String?, val security: String?, val government: String?,
    val economy: String?
) {
    companion object {
        fun fromEDSMSystem(res: EDSMSystemResponse): SystemFinderResult {
            var allegiance: String? = null
            var security: String? = null
            var government: String? = null
            var economy: String? = null
            if (res.Information != null) {
                allegiance = res.Information.Allegiance
                government = res.Information.Government
                security = res.Information.Security
                economy = res.Information.Economy
            }
            return SystemFinderResult(
                res.Name, res.Id, res.PermitRequired, allegiance,
                security, government, economy
            )
        }
    }
}

