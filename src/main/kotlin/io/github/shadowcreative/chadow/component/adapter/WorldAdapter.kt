package io.github.shadowcreative.chadow.component.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import io.github.shadowcreative.chadow.component.JsonCompatibleSerializer
import org.bukkit.Bukkit
import org.bukkit.World
import java.lang.reflect.Type
import java.util.*

class WorldAdapter : JsonCompatibleSerializer<World>(World::class.java)
{
    override fun serialize(src: World, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        val obj = JsonObject()
        obj.addProperty("worldName", src.name)
        obj.addProperty("worldUniqueId", src.uid.toString())
        return obj
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): World
    {
        val obj = json as JsonObject
        return Bukkit.getServer().getWorld(UUID.fromString(obj.get("worldUniqueId").asString))
    }
}