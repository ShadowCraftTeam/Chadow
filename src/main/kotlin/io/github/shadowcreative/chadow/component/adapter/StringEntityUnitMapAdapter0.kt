package io.github.shadowcreative.chadow.component.adapter

import com.google.gson.*
import io.github.shadowcreative.eunit.EntityUnit
import io.github.shadowcreative.eunit.EntityUnitCollection
import java.lang.reflect.Type

internal class StringEntityUnitMapAdapter0<V : EntityUnit<*>>(private val sample: Class<out V>) : JsonSerializer<Map<String, V>>, JsonDeserializer<Map<String, V>>
{
    override fun serialize(p0: Map<String, V>, p1: Type?, p2: JsonSerializationContext?): JsonElement
    {
        val jsonObject = JsonObject()
        for(key in p0.keys)
        {
            val value = p0[key] ?: continue
            val serializeObject = value.toSerializeElements()
            jsonObject.add(key, serializeObject)
        }
        return jsonObject
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(p0: JsonElement, p1: Type?, p2: JsonDeserializationContext?): Map<String, V> {
        val hMap = HashMap<String, V>()
        val entrySet = p0.asJsonObject.entrySet()
        for((key, value) in entrySet) {
            val deserializeValue = EntityUnitCollection.deserialize(value, sample)
            hMap[key] = deserializeValue as V
        }
        return hMap
    }
}
