package io.github.shadowcreative.shadow.entity

import com.google.gson.*
import io.github.shadowcreative.shadow.component.adapter.FileAdapter
import io.github.shadowcreative.shadow.component.adapter.LocationAdapter
import io.github.shadowcreative.shadow.component.adapter.PlayerAdapter
import io.github.shadowcreative.shadow.component.adapter.WorldAdapter
import io.github.shadowcreative.shadow.component.JsonCompatibleSerializer
import io.github.shadowcreative.shadow.entity.coll.ECollection
import io.github.shadowcreative.shadow.platform.GenericInstance
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

abstract class EntityObject<EntityType : EntityObject<EntityType>> : GenericInstance<EntityType>(), JsonDeserializer<EntityType>, JsonSerializer<EntityType>
{
    private var uuid : String? = UUID.randomUUID().toString()

    @Transient
    protected val coll : List<EntityType> = ArrayList()
    fun getEntityCollection() : List<EntityType> = this.coll

    @Transient
    protected val eCollection : ECollection<*>? = null
    fun getEntityReference() : ECollection<*>? = this.eCollection

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
    override fun serialize(src: EntityType, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        return src.toSerialize()
    }

    fun toSerialize() : JsonElement
    {
        return this.serialize0(this::class.java, this)
    }

    fun getEntityFields(target : Class<*> = this::class.java) : Iterable<Field>
    {
        return this.getFields0(target, true)
    }

    fun getSerializableEntityFields(target : Class<*> = this::class.java) : Iterable<Field>
    {
        return this.getFields0(target, false)
    }

    private fun getFields0(base : Class<*>, ignoreTransient: Boolean) : Iterable<Field>
    {
        val fList = ArrayList<Field>()
        var kClass : Class<*> = base
        val modifierField = Field::class.java.getDeclaredField("modifiers")
        while(true) {
            if(ignoreTransient)
                fList.addAll(kClass.declaredFields)
            else {
                for(f in kClass.declaredFields) {
                    f.isAccessible = true
                    val modifierInt = modifierField.getInt(f)
                    if(! Modifier.isTransient(modifierInt)) fList.add(f)
                }
            }
            kClass = kClass.superclass
            if(kClass == EntityObject::class.java) break
        }
        return fList
    }

    private fun serialize0(fs : Class<*>, target : Any = this) : JsonElement
    {
        val jsonObject = JsonObject()
        for(f in this.getSerializableEntityFields(fs))
            addFieldProperty(jsonObject, f, target)
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
}