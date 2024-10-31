package com.example.faghamsac.modules.invoice.model
import java.math.BigDecimal

data class Respuesta(
    var codigobBarras: String? = null,
    var firma: String? = null,
    var hash: String? = null,
    var identificador: String? = null,
    var numero: Long? = null,
    var serie: String? = null,
    var tipoDocumento: String? = null,
    var total: BigDecimal = BigDecimal.ZERO
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Respuesta) return false

        return (codigobBarras == other.codigobBarras &&
                firma == other.firma &&
                hash == other.hash &&
                identificador == other.identificador &&
                numero == other.numero &&
                serie == other.serie &&
                tipoDocumento == other.tipoDocumento &&
                total == other.total)
    }

    override fun hashCode(): Int {
        return (codigobBarras?.hashCode() ?: 0) * 31 +
                (firma?.hashCode() ?: 0) * 31 +
                (hash?.hashCode() ?: 0) * 31 +
                (identificador?.hashCode() ?: 0) * 31 +
                (numero?.hashCode() ?: 0) * 31 +
                (serie?.hashCode() ?: 0) * 31 +
                (tipoDocumento?.hashCode() ?: 0) * 31 +
                total.hashCode()
    }

    override fun toString(): String {
        return "Respuesta(codigobBarras=$codigobBarras, firma=$firma, hash=$hash, identificador=$identificador, numero=$numero, serie=$serie, tipoDocumento=$tipoDocumento, total=$total)"
    }
}
