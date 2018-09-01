package io.github.shadowcreative.eunit

import com.google.common.collect.ArrayListMultimap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import io.github.shadowcreative.chadow.sendbox.ExternalExecutor
import io.github.shadowcreative.chadow.sendbox.SafetyExecutable
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

open class EntityUnitCollection<E : EntityUnit<E>> : ExternalExecutor
{
    private val persistentBaseClass : Class<E> = (javaClass.genericSuperclass as? ParameterizedType)!!.actualTypeArguments[0] as Class<E>
    fun getPersistentBaseClass() : Class<E> = this.persistentBaseClass

    override fun onInit(handleInstance: Any?): Any?
    {
        return super.onInit(this)
    }

    @Synchronized
    fun onChangeHandler(targetClazz : Class<E>? = this.getPersistentBaseClass()) : Map<String, Boolean>?
    {
        val instancePlugin = this.getHandlePlugin()
        if(instancePlugin == null)
        {
            Logger.getGlobal().log(Level.WARNING, "The instance plugin was unhandled, Is it register your plugin?")
            return null
        }
        val pluginFolder = instancePlugin.dataFolder
        val pluginName = instancePlugin.name

        if(targetClazz == null) return null
        val result = this.call("onChangeHandler0", "$pluginFolder\\$pluginName@" + targetClazz.typeName) as? String ?: return null
        val jsonObject = JsonParser().parse(result).asJsonObject
        val map = HashMap<String, Boolean>()
        for((key, value) in jsonObject.entrySet())
        {
            map[key] = (value as JsonObject).get("isChanged").asBoolean
        }

        return map
    }

    @SafetyExecutable(libname = "Chadow.Internal.Core")
    private external fun onChangeHandler0(value0 : String) : String

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

        this.entityCollection!!.add(entity)
        return true
    }

    constructor() : this(UUID.randomUUID().toString().replace("-", ""))

    protected constructor(uuid : String) : super()
    {
        this.uuid = uuid
        this.entityCollection = ArrayList()
    }

    @Synchronized fun gerenate() : EntityUnitCollection<E>
    {
        pluginCollections.put(this.getHandlePlugin(), this)
        return this
    }

    protected fun setIdentifiableObject(vararg fieldString : String) {
        this.identifier.addAll(fieldString)
    }

    private val uuid : String
    fun getUniqueId() : String = this.uuid

    private var entityCollection : MutableList<EntityUnit<E>>? = null
    fun getEntities() : MutableList<EntityUnit<E>>? = this.entityCollection

    private val identifier : MutableList<String> = ArrayList()
    fun getIdentifier() : MutableList<String> = this.identifier

    open fun getEntity(objectData: Any?) : E?
    {
        if(objectData == null) return null
        @Suppress("UNCHECKED_CAST")
        return EntityUnitCollection.getEntity0(objectData, this.getPersistentBaseClass())
    }

    companion object
    {
        private val pluginCollections : ArrayListMultimap<IntegratedPlugin, EntityUnitCollection<*>> = ArrayListMultimap.create()
        fun getEntityCollections() : ArrayListMultimap<IntegratedPlugin, EntityUnitCollection<*>> = pluginCollections

        fun <U> deserialize(element : JsonElement, reference: Class<U>) : U?
        {
            return null
        }

        fun <E> getEntity0(objectData: Any, refClazz : Class<E>): E?
        {
            try
            {
                return null
            }
            catch(e : TypeCastException)
            {
                return null
            }
        }

        fun asReference(entity: EntityUnit<*>)
        {
            for(k in getEntityCollections().values()) {
                if(entity::class.java.isAssignableFrom(k.getPersistentClass())) {
                    if(! k.isEnabled()) {
                        // Hook the reference collection.
                        var eField = entity::class.java.superclass.getDeclaredField("eCollection")
                        eField.isAccessible = true
                        eField.set(entity, k)

                        // Generate the unique signature if the entity have no id.
                        eField = entity::class.java.superclass.getDeclaredField("uuid")
                        eField.isAccessible = true
                        eField.set(entity, UUID.randomUUID().toString().replace("-", ""))
                    }
                    else {
                        val messageHandler = k.getHandlePlugin()!!.getMessageHandler()
                        messageHandler.sendMessage("The EntityUnitCollection<${k.getPersistentClass()}> was disabled, It couldn't register your entity.")
                    }
                }
            }
            Logger.getGlobal().log(Level.WARNING, "Not exist EntityUnitCollection<${entity::class.java.simpleName}>," +
                    " It needs to specific entity collection from register class.")
        }
    }
}
