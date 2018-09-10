package io.github.shadowcreative.eunit

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.shadowcreative.chadow.component.Internal
import io.github.shadowcreative.chadow.component.JsonCompatibleSerializer
import io.github.shadowcreative.chadow.component.adapter.FileAdapter
import io.github.shadowcreative.chadow.component.adapter.LocationAdapter
import io.github.shadowcreative.chadow.component.adapter.PlayerAdapter
import io.github.shadowcreative.chadow.component.adapter.WorldAdapter
import io.github.shadowcreative.chadow.config.SynchronizeReader
import org.bukkit.Bukkit
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

/**
 * EntityUnit can automatically serialize classes and make them into files.
 * The Data is managed in real-time by SynchronizeReader.
 *
 * @param EntityType Inherits the class
 * @See io.github.shadowcreative.chadow.config.SynchronizeReader
 */
abstract class EntityUnit<EntityType : EntityUnit<EntityType>> : SynchronizeReader
{
    @Suppress("LeakingThis", "UNCHECKED_CAST")
    @Synchronized open fun create(objectId : String = uuid) : EntityUnit<EntityType>
    {
        EntityUnitCollection.asReference(this)
        if(this.eCollection == null) Logger.getGlobal().log(Level.WARNING,"The reference collection unhandled")
        else this.eCollection.registerObject(this)
        if(this.toSerialize()) {
            Logger.getGlobal().log(Level.INFO, "Registered Object -> ${this}")
        }
        else {
            Logger.getGlobal().log(Level.SEVERE, "Failed Registering Object -> ${this}")
        }
        return this
    }

    private val uuid : String
    fun getUniqueId() : String = this.uuid

    @Transient protected val eCollection : EntityUnitCollection<EntityType>? = null
    fun getEntityReference() : EntityUnitCollection<EntityType>? = this.eCollection

    fun getEntity(obj : Any?) : EntityType?
    {
        val ref = this.eCollection ?: return null
        return ref.getEntity(obj)
    }

    /**
     *
     */
    fun hasSerializableField(name : String, equalsIgnoreCase : Boolean = true) : Boolean
    {
        for(field in this.getSerializableEntityFields())
            if(field.name.equals(name, ignoreCase = equalsIgnoreCase)) return true
        return false
    }

    @Transient private val adapterColl : MutableList<JsonCompatibleSerializer<*>> = ArrayList()
    fun registerAdapter(vararg adapters : KClass<out JsonCompatibleSerializer<*>>)
    {
        for(kClass in adapters) {
            val adapterConstructor : KFunction<JsonCompatibleSerializer<*>>? = kClass.primaryConstructor
            if(adapterConstructor != null && adapterConstructor.parameters.isEmpty())
                adapterColl.add(adapterConstructor.call())

        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerAdapter(vararg adapters : Class<out JsonCompatibleSerializer<*>>)
    {
        for(kClass in adapters) {
            val adapterConstructor = kClass.constructors[0]
            if(adapterConstructor != null && adapterConstructor.parameters.isEmpty())
                adapterColl.add(adapterConstructor.newInstance() as JsonCompatibleSerializer<*>)
        }
    }

    companion object
    {
        fun registerDefaultAdapter(gsonBuilder : GsonBuilder) : GsonBuilder {
            for(adapter in getDefaultAdapter()) {
                val jcs = adapter.constructors[0].newInstance() as JsonCompatibleSerializer<*>
                gsonBuilder.registerTypeAdapter(jcs.getReference(), jcs)
            }
            return gsonBuilder
        }

        fun getDefaultAdapter() : Array<Class<out JsonCompatibleSerializer<*>>> {
            return arrayOf(LocationAdapter::class.java, PlayerAdapter::class.java, WorldAdapter::class.java, FileAdapter::class.java)
        }


        fun isInternalField(f: Field): Boolean {
            return f.isAnnotationPresent(Internal::class.java)
        }

        fun setProperty(jsonObject : JsonObject, key : String, value : Any?, adapterColl : List<JsonCompatibleSerializer<*>>? = null)
        {
            val gsonBuilder = GsonBuilder()

            var adapters = adapterColl
            if(adapters == null)
                adapters = ArrayList()

            for(adapter in adapters) {
                val adapterType = adapter.getReference()
                gsonBuilder.registerTypeAdapter(adapterType, adapter)
            }
            val gson = gsonBuilder.serializeNulls().create()
            when(value) {
                is Number -> jsonObject.addProperty(key, value)
                is Char -> jsonObject.addProperty(key, value)
                is String -> jsonObject.addProperty(key, value)
                is Boolean -> jsonObject.addProperty(key, value)
                else -> {
                    if(value is EntityUnit<*>)
                    {
                        jsonObject.add(key, value.toSerializeElements())
                        return
                    }
                    else {
                        try {
                            val result = gson.toJson(value)
                            val parser = JsonParser()
                            val element = parser.parse(result)
                            jsonObject.add(key, element)
                        }
                        catch(e : Exception) {
                            e.printStackTrace()
                            jsonObject.addProperty(key, "FAILED_SERIALIZED_OBJECT")
                        }
                    }
                }
            }
        }
    }

    fun toSerializeElements() : JsonElement
    {
        return this.serialize0(this::class.java, this)
    }

    fun getEntityFields(target : Class<*> = this::class.java) : Iterable<Field>
    {
        return this.getFields0(target, true)
    }

    fun getSerializableEntityFields(target : Class<*> = this::class.java, specific : List<String>? = null) : Iterable<Field>
    {
        val fieldList = this.getFields0(target, false)
        return if(specific == null) fieldList
        else fieldList.filter { field: Field -> specific.contains(field.name)  }
    }

    private fun getFields0(base : Class<*>, ignoreTransient: Boolean) : Iterable<Field>
    {
        val fList = ArrayList<Field>()
        var kClass : Class<*> = base
        val modifierField = Field::class.java.getDeclaredField("modifiers")
        modifierField.isAccessible = true
        while(true) {
            if(ignoreTransient)
                for(f in kClass.declaredFields) {
                    if(f.type.name.endsWith("\$Companion"))
                        continue
                    else { if(! isInternalField(f)) fList.add(f) }
                }
            else {
                for(f in kClass.declaredFields) {
                    f.isAccessible = true
                    if(f.type.name.endsWith("\$Companion"))
                        continue
                    val modifierInt = modifierField.getInt(f)
                    if(! Modifier.isTransient(modifierInt) && ! isInternalField(f)) fList.add(f)
                }
            }
            if(kClass == EntityUnit::class.java) break
            kClass = kClass.superclass
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

    private fun addFieldProperty(jsonObject : JsonObject, field : Field, target : Any) {
        val fieldName : String = field.name
        val value: Any? = try { field.get(target) } catch(e : IllegalArgumentException) { null } catch(e2 : IllegalAccessException) { null }
        if(value == null) {
            jsonObject.addProperty(fieldName, "INVALID_SERIALIZED_VALUE"); return
        }
        setProperty(jsonObject, fieldName, value, this.adapterColl)
    }

    final override fun serialize(): String
    {
        return GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(this.toSerializeElements())
    }

    fun apply(serialize : String)
    {
        val fields = JsonParser().parse(serialize)
        return this.apply(fields)
    }

    fun apply(fields : JsonElement)
    {
        val newInstance = EntityUnitCollection.deserialize(fields, this::class.java) ?: throw RuntimeException("Cannot create new instance from" +
                " deserialize Class<${this::class.java.name}> function")
        return this.apply(newInstance)
    }

    @Synchronized fun apply(victim : EntityUnit<EntityType>)
    {
        if(victim::class.java == this::class.java) {
            for (k in victim.getSerializableEntityFields()) this.applyThis(k, victim)
        }
    }

    private fun applyThis(field : Field, target : Any?) : Boolean
    {
        return try { field.isAccessible = true; field.set(this, field.get(target)); true } catch(e : Exception) { false }
    }

    /**
     * This method is executed when deserialization is finished.
     * It is executed indirectly by Collection, and it is not recommended to execute it directly.
     */
    protected open fun after() {

    }

    @Transient
    private var serviceRuntimeTaskId : Int = -1

    @Transient
    private var watchedKey : WatchKey? = null

    override fun onInit(handleInstance: Any?): Any?
    {
        if(this.enabledRefreshMode() && this.getFileService() != null) {
            if(this.serviceRuntimeTaskId != -1) {
                if(watchedKey != null)
                {
                    for (event in watchedKey!!.pollEvents()) {
                        val contextPath = event.context() as? Path
                        if(contextPath == null || !contextPath.endsWith(this.getFile().name)) {
                            continue
                        }
                        val kind = event.kind()
                        when (kind) {
                            StandardWatchEventKinds.ENTRY_MODIFY -> {
                                Logger.getGlobal().log(Level.WARNING, "The object file will be changed and the data will be reloaded")
                                val inputStreamReader = InputStreamReader(FileInputStream(File(this.getSubstantialPath(), this.getFile().path)))
                                val sBuffer = StringBuilder()
                                val b = CharArray(4096)
                                while (true) {
                                    val i = inputStreamReader.read(b)
                                    if (i == -1) break
                                    sBuffer.append(String(b, 0, i))
                                }
                                this.apply(JsonParser().parse(sBuffer.toString()))
                            }
                            StandardWatchEventKinds.ENTRY_DELETE -> {
                                Logger.getGlobal().log(Level.WARNING, "The file has been deleted, " +
                                        "It is presumably attributed to an artifact or an error unknown to the system.")
                                this.setEnabled(false)
                            }
                            StandardWatchEventKinds.OVERFLOW -> {

                            }
                        }
                    }
                    this.serviceRuntimeTaskId = -1
                    Bukkit.getScheduler().cancelTask(this.serviceRuntimeTaskId)
                    val result = watchedKey!!.reset()
                    this.watchedKey = null

                    return result
                }
            }
            else
            {
                val serviceTakenListener = Runnable { this.watchedKey = this.getFileService()!!.take() }
                val task = Bukkit.getScheduler().runTaskAsynchronously(this.activePlugin, serviceTakenListener)
                this.serviceRuntimeTaskId = task.taskId
                return true
            }
        }
        return false
    }

    constructor() : this(UUID.randomUUID().toString())

    constructor(uniqueId : String) : super(uniqueId)
    {
        this.registerAdapter(*EntityUnit.getDefaultAdapter())
        this.uuid = uniqueId.replace(".", "")
    }
}