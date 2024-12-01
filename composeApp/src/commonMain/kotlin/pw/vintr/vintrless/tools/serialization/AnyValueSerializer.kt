package pw.vintr.vintrless.tools.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import pw.vintr.vintrless.domain.v2ray.model.V2rayConfig

/**
 * Convert Any? to JsonPrimitive
 */
private fun Any?.toJsonPrimitive(): JsonPrimitive {
    return when (this) {
        null -> JsonNull
        is JsonPrimitive -> this
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        // add custom convert
        else -> throw Exception("Bad value: ${this::class}")
    }
}

private fun JsonPrimitive.toAnyValue(): Any? {
    val content = this.content
    if (this.isString){
        // add custom string convert
        return content
    }
    if (content.equals("null", ignoreCase = true)) {
        return null
    }
    if (content.equals("true", ignoreCase = true)) {
        return true
    }
    if (content.equals("false", ignoreCase = true)) {
        return false
    }
    val intValue = content.toIntOrNull()
    if (intValue!=null){
        return intValue
    }
    val longValue = content.toLongOrNull()
    if (longValue!=null){
        return longValue
    }
    val doubleValue = content.toDoubleOrNull()
    if (doubleValue!=null){
        return doubleValue
    }
    throw Exception("Bad value: $content")
}

object AnyValueSerializer : KSerializer<Any?> {

    private val delegateSerializer = JsonPrimitive.serializer()

    override val descriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Any?) {
        encoder.encodeSerializableValue(delegateSerializer, value.toJsonPrimitive())
    }
    override fun deserialize(decoder: Decoder): Any? {
        val jsonPrimitive = decoder.decodeSerializableValue(delegateSerializer)
        return jsonPrimitive.toAnyValue()
    }
}

/**
 * Convert Any? to JsonElement
 */
private fun Any?.toJsonElement(): JsonElement {
    return when (this) {
        null -> JsonNull
        is JsonElement -> this
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
        // !!! key simply converted to string
        is Map<*, *> -> JsonObject(this.map { it.key.toString() to it.value.toJsonElement() }.toMap())
        is V2rayConfig.DnsBean.ServersBean -> Json.encodeToJsonElement(this)
        else -> throw Exception("Bad value: ${this::class}=${this}}")
    }
}

private fun JsonElement.toAnyOrNull(): Any? {
    return when (this) {
        is JsonNull -> null
        is JsonPrimitive -> toAnyValue()
        // !!! key convert back custom object
        is JsonObject -> this.map { it.key to it.value.toAnyOrNull() }.toMap()
        is JsonArray -> this.map { it.toAnyOrNull() }
    }
}

object AnySerializer : KSerializer<Any?> {

    private val delegateSerializer = JsonElement.serializer()

    override val descriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Any?) {
        encoder.encodeSerializableValue(delegateSerializer, value.toJsonElement())
    }

    override fun deserialize(decoder: Decoder): Any? {
        val jsonPrimitive = decoder.decodeSerializableValue(delegateSerializer)
        return jsonPrimitive.toAnyOrNull()
    }
}
