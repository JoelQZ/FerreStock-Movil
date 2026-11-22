package pe.ferrestock.app.domain.model

enum class Rol {
    ADMINISTRADOR,
    CAJERO;

    companion object {
        fun fromString(value: String?): Rol {
            return when (value?.uppercase()?.trim()) {
                "ADMINISTRADOR", "ADMIN" -> ADMINISTRADOR
                "CAJERO", "CAJA" -> CAJERO
                else -> CAJERO
            }
        }
    }
}
