package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

fun <T> JsonElement.parseList(json: Json, serializer: KSerializer<T>): List<T> {
    return when (this) {
        is JsonArray -> json.decodeFromJsonElement(ListSerializer(serializer), this)
        is JsonObject -> {
            val arrayElement = this["results"] ?: this["productos"] ?: this["data"] ?: this["ventas"] ?: this["turnos"]
            if (arrayElement is JsonArray) {
                json.decodeFromJsonElement(ListSerializer(serializer), arrayElement)
            } else {
                emptyList()
            }
        }
        else -> emptyList()
    }
}
