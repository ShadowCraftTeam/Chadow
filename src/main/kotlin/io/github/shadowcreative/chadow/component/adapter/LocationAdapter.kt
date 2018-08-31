package io.github.shadowcreative.chadow.component.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import io.github.shadowcreative.chadow.component.JsonCompatibleSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

class LocationAdapter : JsonCompatibleSerializer<Location>(Location::class.java)
{
    override fun serialize(src: Location, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        val obj = JsonObject()
        if(src.world != null) {
            obj.addProperty("worldName", src.world.name)
            obj.addProperty("worldUniqueId", src.world.uid.toString())
        }
        obj.addProperty("loc-x", src.x)
        obj.addProperty("loc-y", src.y)
        obj.addProperty("loc-z", src.z)
        obj.addProperty("loc-yaw", src.yaw)
        obj.addProperty("loc-pitch", src.pitch)
        return obj
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Location
    {
        val obj = json as JsonObject
        return Location(Bukkit.getServer().getWorld(obj.get("worldUniqueId").asString), obj.get("loc-x").asDouble, obj.get("loc-y").asDouble,
                obj.get("loc-z").asDouble, obj.get("loc-yaw").asFloat, obj.get("loc-pitch").asFloat)
    }
}