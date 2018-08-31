package io.github.shadowcreative.chadow.component.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import io.github.shadowcreative.chadow.component.JsonCompatibleSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.reflect.Type
import java.util.*

class PlayerAdapter : JsonCompatibleSerializer<Player>(Player::class.java)
{
    override fun serialize(src: Player, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        val obj = JsonObject()
        obj.addProperty("playerId", src.uniqueId.toString())
        return obj
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Player
    {
        val obj = json as JsonObject
        return Bukkit.getServer().getPlayer(UUID.fromString(obj.get("playerId").asString))
    }
}