package io.github.shadowcreative.shadow.entity

import com.google.gson.*
import io.github.shadowcreative.shadow.component.JsonCompatibleSerializer
import io.github.shadowcreative.shadow.adapter.FileAdapter
import io.github.shadowcreative.shadow.adapter.LocationAdapter
import io.github.shadowcreative.shadow.adapter.PlayerAdapter
import io.github.shadowcreative.shadow.adapter.WorldAdapter
import io.github.shadowcreative.shadow.platform.GenericInstance
import java.lang.reflect.Field
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.JsonElement
import com.google.gson.JsonParser


abstract class EntityObject<EntityType : EntityObject<EntityType>> : GenericInstance<EntityType>(), JsonDeserializer<EntityType>, JsonSerializer<EntityType>
{
    @Transient
    private val uuid : String = UUID.randomUUID().toString()

    @Transient
    protected val coll : List<EntityType> = ArrayList()

    private val adapterColl : MutableList<JsonCompatibleSerializer<*>> = ArrayList()
    protected fun addRegisterAdapter(jcs : JsonCompatibleSerializer<*>) = this.adapterColl.add(jcs)

    init
    {
        this.adapterColl.add(LocationAdapter())
        this.adapterColl.add(PlayerAdapter())
        this.adapterColl.add(WorldAdapter())
        this.adapterColl.add(FileAdapter())
    }

    companion object
    {
        fun setProperty(jsonObject : JsonObject, key : String, value : Any?, adapterColl : List<JsonCompatibleSerializer<*>>? = null)
        {
            val gsonBuilder = GsonBuilder()

            var adapters = adapterColl
            if(adapters == null)
               adapters = ArrayList()

            for(adapter in adapters) {
                val adapterType = adapter.getPersistentClass()
                if(adapterType != null)
                    gsonBuilder.registerTypeAdapter(adapterType, adapter)
            }
            val gson = gsonBuilder.serializeNulls().create()
            when(value) {
                is Number -> jsonObject.addProperty(key, value)
                is Char -> jsonObject.addProperty(key, value)
                is String -> jsonObject.addProperty(key, value)
                is Boolean -> jsonObject.addProperty(key, value)
                else -> {
                    if(value is EntityObject<*>)
                    {
                        jsonObject.add(key, value.toSerialize())
                        return
                    }
                    else {
                        try {
                            val result = gson.toJson(value)
                            val parser = JsonParser()
                            val element = parser.parse(result)
                            jsonObject.add(key, element)
                        }
                        catch(e : Exception)
                        {
                            e.printStackTrace()
                            jsonObject.addProperty(key, "FAILED_SERIALIZED_OBJECT")
                        }
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getEntity(value : Any?) : EntityType?
    {
        var i : EntityType? = null
        val refiled = this.getPersistentClass()
        if(refiled != null)
        {
            val constructor = refiled.constructors[0]
            i = constructor.newInstance() as EntityType
        }
        return i
    }

    override fun serialize(src: EntityType, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        return src.toSerialize()
    }

    fun toSerialize() : JsonElement
    {
        return this.serialize0(this::class.java.declaredFields, this)
    }

    private fun serialize0(fs : Array<Field>, target : Any = this) : JsonElement
    {
        val jsonObject = JsonObject()
        for(f in fs) {
            f.isAccessible = true
            addFieldProperty(jsonObject, f, this)
        }
        return jsonObject
    }

    private fun addFieldProperty(jsonObject : JsonObject, field : Field, target : Any)
    {
        val fieldName : String = field.name
        val value: Any? = try { field.get(target) } catch(e : IllegalArgumentException) { null } catch(e2 : IllegalAccessException) { null }
        if(value == null) {
            jsonObject.addProperty(fieldName, "INVALID_SERIALIZED_VALUE"); return
        }
        setProperty(jsonObject, fieldName, value, this.adapterColl)
    }

    private fun applySerializedValues(safetyMode : Boolean = true) : Boolean
    {
        val workstation = fun()
        {

        }

        return try {
            if (safetyMode) {
                synchronized(this) {
                    workstation()
                }
            } else {
                workstation()
            }
            true
        }
        catch(e : Exception) {
            false
        }
    }
}