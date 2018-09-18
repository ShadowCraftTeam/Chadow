package io.github.shadowcreative.eunit

import com.google.common.collect.ArrayListMultimap
import com.google.gson.*
import com.google.gson.stream.JsonReader
import io.github.shadowcreative.chadow.component.Internal
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import io.github.shadowcreative.chadow.sendbox.ExternalExecutor
import io.github.shadowcreative.chadow.util.ReflectionUtility
import io.github.shadowcreative.chadow.util.StringUtility
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.nio.file.*
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("UNCHECKED_CAST")
open class EntityUnitCollection<E : EntityUnit<E>> : ExternalExecutor
{
    private val persistentBaseClass : Class<E> = (javaClass.genericSuperclass as? ParameterizedType)!!.actualTypeArguments[0] as Class<E>
    fun getPersistentBaseClass() : Class<E> = this.persistentBaseClass

    private var service : WatchService? = null
    fun getFileService() : WatchService? = this.service

    @Internal
    private var watchedKey : WatchKey? = null

    @Internal
    private var serviceRuntimeTaskId : Int = -1

    private fun registerService() {
        if(this.service == null) {
            val service = FileSystems.getDefault().newWatchService()
            val path = Paths.get(File(this.getHandlePlugin()!!.dataFolder,
                    "storedata/${this.getHandlePlugin()!!.name}@${this.getPersistentBaseClass().name}").toURI())
            path.register(service, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE)
            this.service = service
        }
    }

    private fun onInitService() : Boolean
    {
        if(this.service == null) return false

        if(this.serviceRuntimeTaskId != -1) {
            if(watchedKey != null) {
                for(event in watchedKey!!.pollEvents()) {
                    val contextPath = event.context() as? Path ?: continue
                    val contextPathString = contextPath.toString()
                    for(entity in this.entityCollection) {
                        if(!entity.enabledRefreshMode()) continue
                        if(contextPathString != entity.getFile().name + ".json") continue
                        val kind = event.kind()
                        when (kind) {
                            StandardWatchEventKinds.ENTRY_MODIFY -> {
                                Logger.getGlobal().log(Level.WARNING, "The object file will be changed and the data will be reloaded")
                                val inputStreamReader = InputStreamReader(FileInputStream(File(entity.getSubstantialPath(), entity.getFile().path)))
                                val sBuffer = StringBuilder()
                                val b = CharArray(4096)
                                while (true) {
                                    val i = inputStreamReader.read(b)
                                    if (i == -1) break
                                    sBuffer.append(String(b, 0, i))
                                }
                                entity.apply(JsonParser().parse(sBuffer.toString()))
                            }

                            StandardWatchEventKinds.ENTRY_DELETE -> {
                                Logger.getGlobal().log(Level.WARNING, "The file has been deleted, " +
                                        "It is presumably attributed to an artifact or an error unknown to the system.")
                                entity.internalModified = true
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
        }
        else
        {
            val serviceTakenListener = Runnable { this.watchedKey = this.getFileService()!!.take() }
            val task = Bukkit.getScheduler().runTaskAsynchronously(this.getHandlePlugin(), serviceTakenListener)
            this.serviceRuntimeTaskId = task.taskId
            return true
        }
        return false
    }

    final override fun onInit(handleInstance: Any?): Any?
    {
        val plugin = this.getHandlePlugin() ?: return false
        if(this.entityCollection.size > 0) { return false }
        val workspace = File(plugin.dataFolder, "storedata/${plugin.name}@${this.getPersistentBaseClass().name}")
        if(! workspace.exists()) workspace.mkdirs()
        synchronized(this.entityCollection)
        {
            for (jsonFile in workspace.listFiles()) {
                try {
                    val parsedValue = JsonParser().parse(JsonReader(FileReader(jsonFile)))
                    val entity = EntityUnitCollection.deserialize(parsedValue, this.persistentBaseClass)
                    if (entity == null) {
                        continue
                    } else {
                        this.entityCollection.add(entity)
                    }
                } catch (e: JsonSyntaxException) {
                    continue
                }

            }
        }
        return true
    }

    private fun isRegisterObject(entity : E) : Boolean {
        return this.entityCollection.contains(entity)
    }


    @Suppress("unused")
    fun onChangeHandler(targetClazz : Class<E>? = this.getPersistentBaseClass()) : Map<String, Boolean>?
    {
        val instancePlugin = this.getHandlePlugin()
        if(instancePlugin == null)
        {
            Logger.getGlobal().log(Level.WARNING, "The instance plugin was unhandled, Is it registered your plugin?")
            return null
        }
        val pluginFolder = instancePlugin.dataFolder
        val pluginName = instancePlugin.name

        if(targetClazz == null) return null
        val result = this.safetyCall("onChangeHandler0", "$pluginFolder\\$pluginName@" + targetClazz.typeName) as? String ?: return null
        val jsonObject = JsonParser().parse(result).asJsonObject
        val map = HashMap<String, Boolean>()
        for((key, value) in jsonObject.entrySet()) {
            map[key] = (value as JsonObject).get("isChanged").asBoolean
        }
        return map
    }

    fun registerObject(entity: EntityUnit<E>): Boolean
    {
        var handlePlugin = this.getHandlePlugin()

        if(this.getHandlePlugin() == null)
            handlePlugin = IntegratedPlugin.CorePlugin

        if(handlePlugin == null)
            println("Warning: The controlled plugin was unhandled -> " + "${entity::class.java.typeName}@${entity.getUniqueId()}")
        else {
            entity.setPlugin(handlePlugin)
        }

        this.entityCollection.add(entity)
        return true
    }

    constructor() : this(UUID.randomUUID().toString().replace("-", ""))

    protected constructor(uuid : String) : super()
    {
        this.uuid = uuid
        this.entityCollection = ArrayList()
    }

    @Synchronized fun generate() : EntityUnitCollection<E>
    {
        if(this.getHandlePlugin() == null) Logger.getGlobal().log(Level.INFO, "To register collection, Please define you want activate plugin")
        pluginCollections.put(this.getHandlePlugin(), this)
        return this
    }

    protected fun setIdentifiableObject(vararg fieldString : String) {
        this.identifier.addAll(fieldString)
    }

    private val uuid : String
    fun getUniqueId() : String = this.uuid


    private var entityCollection : MutableList<EntityUnit<E>>

    /**
     * Retrieves all entities that have the class type of the Collection.
     * The entities are those in which disk I/O synchronization is continuously performed by the create function.
     *
     * @return The entities with continuous disk I/O synchronization
     * @see io.github.shadowcreative.eunit.EntityUnit.create
     */
    fun getEntities() : MutableList<EntityUnit<E>> = this.entityCollection

    private val identifier : MutableList<String> = ArrayList()
    fun addIdentity(vararg signature : String) = this.identifier.addAll(signature)
    fun getIdentifier() : MutableList<String> = this.identifier

    private var monitorTask : BukkitTask? = null

    private fun monitor(enabled: Boolean) {
        if(enabled) {
            if(this.monitorTask != null) {
                this.monitorTask!!.cancel()
            }
            val runnable = {
                while (true) {
                    this.onInitService()
                }
            }
            this.monitorTask = Bukkit.getScheduler().runTaskAsynchronously(this.getHandlePlugin(), runnable)
        }
        else {
            if(this.monitorTask != null) {
                this.monitorTask!!.cancel()
                this.monitorTask = null
            }
        }
    }

    override fun isEnabled(): Boolean {
        return EntityUnitCollection.pluginCollections.containsEntry(this.getHandlePlugin(), this)
    }

    override fun setEnabled(active: Boolean) {
        //super.setEnabled(active)
        if(active) {
            this.registerService()
            this.monitor(active)
            this.onInit(null)
            EntityUnitCollection.pluginCollections.put(this.getHandlePlugin(), this)
        }
        else {
            val service = this.getFileService()
            if(service != null) {
                service.close()
                this.service = null
                this.monitor(active)
            }
            EntityUnitCollection.pluginCollections.remove(this.getHandlePlugin(), this)
        }
    }

    open fun getEntity(objectData: Any?) : E?
    {
        if(objectData == null) return null
        return EntityUnitCollection.getEntity0(objectData, this.getPersistentBaseClass())
    }

    companion object
    {
        private val pluginCollections : ArrayListMultimap<IntegratedPlugin, EntityUnitCollection<*>> = ArrayListMultimap.create()
        fun getEntityCollections() : ArrayListMultimap<IntegratedPlugin, EntityUnitCollection<*>> = pluginCollections

        fun <U : EntityUnit<*>> deserialize(element : JsonElement, reference: Class<U>) : U?
        {
            val messageHandler = IntegratedPlugin.CorePlugin!!.getMessageHandler()
            var targetObject: U? = null
            val constructColl = reference.constructors
            val toJsonObject = element as JsonObject
            for(constructor in constructColl)
            {
                @Suppress("UNCHECKED_CAST")
                if(constructor.parameterCount == 1) {
                    targetObject = constructor.newInstance(toJsonObject.get("uuid").asString) as? U
                    if(targetObject != null) break
                }
            }
            if(targetObject == null) return null
            for(field in targetObject.getSerializableEntityFields()) {
                val refValue = toJsonObject.get(field.name)
                if(refValue == null) {
                    messageHandler.sendMessage("The variable '${field.name}'[$refValue] was invalid value that compare with base class.")
                    continue
                }
                val modifiersField = Field::class.java.getDeclaredField("modifiers")
                modifiersField.isAccessible = true
                when {
                    EntityUnit::class.java.isAssignableFrom(field.type) -> field.set(targetObject, deserialize(refValue, targetObject::class.java))
                    else -> {
                        val result = EntityUnitCollection.availableSerialize0(refValue, field.type)
                        if(result != null) {
                            if(Modifier.isFinal(field.modifiers))
                                modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
                            val resultClazz = result::class.java
                            field.set(targetObject, resultClazz.cast(result))
                        }
                    }
                }
            }

            //val toBuildMethod = targetObject::class.java.getDeclaredMethod("after")
            //toBuildMethod.isAccessible = true
            //toBuildMethod.invoke(targetObject, Array<Any>(0, fun(_ : Int) {}))
            return targetObject
        }

        private fun availableSerialize0(jsonElement : JsonElement, ref : Class<*>) : Any?
        {
            return try {
                val gson = EntityUnit.registerDefaultAdapter(GsonBuilder()).create()
                gson.fromJson(jsonElement, ref)
            } catch(e : Exception) {
                null
            }
        }

        fun <E : EntityUnit<E>> getEntity0(objectData: Any, refClazz : Class<E>): E?
        {
            try {
                val collection = EntityUnitCollection.getEntityCollection(refClazz) ?: return null
                if(! collection.isEnabled()) {
                    Logger.getGlobal().log(Level.SEVERE, "The EntityCollectionActivator<${refClazz.typeName}> is disabled, Is your plugin turned off or not register?")
                    return null
                }
                val registerEntities = collection.getEntities()
                if(registerEntities.isEmpty()) return null

                val checkFunction0 = fun(value : String, target : EntityUnit<*>) : E? {
                    if(StringUtility.isUUID(value) && target.getUniqueId() == objectData) return target as E?
                    else {
                        for (field in target.getSerializableEntityFields(specific = collection.getIdentifier())) {
                            if(field.type != String::class) continue
                            if((field.get(target) as String) == value) return target as E?
                        }
                    }
                    return null
                }

                val checkFunction1 = fun(_ : Any?, _: EntityUnit<*>) : E? {
                    // Not implemented, It only checks using the string value until now.
                    return null
                }

                for(entity in registerEntities) {
                    when (objectData) {
                        is String -> {
                            if (ReflectionUtility.inlineNullCheck(objectData, entity, checkFunction0)) return entity as E?
                        }
                        else -> {
                            if (ReflectionUtility.inlineNullCheck(objectData, entity, checkFunction1)) return entity as E?
                        }
                    }
                }
                return null
            }
            catch(e : TypeCastException) {
                return null
            }
        }

        fun <U : EntityUnit<U>> getEntityCollection(ref : Class<U>) : EntityUnitCollection<U>?
        {
            for(k in getEntityCollections().values()) {
                if(ref.isAssignableFrom(k.getPersistentBaseClass()))
                    return k as? EntityUnitCollection<U>
            }
            return null
        }

        fun asReference(entity: EntityUnit<*>)
        {
            for(k in getEntityCollections().values()) {
                if(entity::class.java.isAssignableFrom(k.getPersistentClass()))
                {
                    if(k.isEnabled()) {
                        // Hook the reference collection.
                        var eField = entity::class.java.superclass.getDeclaredField("eCollection")
                        eField.isAccessible = true
                        eField.set(entity, k)
                        // Generate the unique signature if the entity have no id.
                        eField = entity::class.java.superclass.getDeclaredField("uuid")
                        eField.isAccessible = true
                        val uuid = eField.get(entity) as? String
                        if(uuid == null) eField.set(entity, UUID.randomUUID().toString().replace("-", ""))
                        return
                    }
                    else
                    {
                        val messageHandlerPlugin = k.getHandlePlugin()
                        val message = "The EntityUnitCollection<${k.getPersistentClass()}> was disabled, It couldn't register your entity."
                        if(messageHandlerPlugin == null) Logger.getGlobal().log(Level.INFO, message)
                        else messageHandlerPlugin.getMessageHandler().sendMessage(message)
                    }
                }
            }
            Logger.getGlobal().log(Level.WARNING, "Not exist EntityUnitCollection<${entity::class.java.simpleName}>," +
                    " It needs to specific entity collection from register class.")
        }
    }

    operator fun get(i : Int) : EntityUnit<E>{
        return this.entityCollection[i]
    }
}
