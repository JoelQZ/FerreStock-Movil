package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

object EnteroFlexible : KSerializer<Int> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EnteroFlexible", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val elemento = (decoder as? JsonDecoder)?.decodeJsonElement() ?: return decoder.decodeInt()
        val texto = (elemento as? JsonPrimitive)?.content ?: return 0
        return texto.toDoubleOrNull()?.toInt() ?: 0
    }

    override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)
}

object TextoFlexible : KSerializer<String?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TextoFlexible", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String? {
        val elemento = (decoder as? JsonDecoder)?.decodeJsonElement() ?: return decoder.decodeString()
        if (elemento is JsonNull) return null
        return (elemento as? JsonPrimitive)?.content
    }

    override fun serialize(encoder: Encoder, value: String?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value)
    }
}
