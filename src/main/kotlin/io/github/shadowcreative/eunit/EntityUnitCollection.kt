package io.github.shadowcreative.eunit

import com.google.common.collect.ArrayListMultimap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import io.github.shadowcreative.chadow.sendbox.ExternalExecutor
import io.github.shadowcreative.chadow.sendbox.SafetyExecutable
import io.github.shadowcreative.chadow.util.ReflectionUtility
import io.github.shadowcreative.chadow.util.StringUtility
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("UNCHECKED_CAST")
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
            Logger.getGlobal().log(Level.WARNING, "The instance plugin was unhandled, Is it registered your plugin?")
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

    @Synchronized fun generate() : EntityUnitCollection<E>
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
        return EntityUnitCollection.getEntity0(objectData, this.getPersistentBaseClass())
    }

    companion object
    {
        private val pluginCollections : ArrayListMultimap<IntegratedPlugin, EntityUnitCollection<*>> = ArrayListMultimap.create()
        fun getEntityCollections() : ArrayListMultimap<IntegratedPlugin, EntityUnitCollection<*>> = pluginCollections

        private fun <U : EntityUnit<*>> deserialize(element : JsonElement, reference: Class<U>) : U?
        {
            val messageHandler = IntegratedPlugin.CorePlugin!!.getMessageHandler()
            var targetObject: U? = null
            val constructColl = reference.constructors
            for(constructor in constructColl)
            {
                @Suppress("UNCHECKED_CAST")
                if(constructor.parameters.isEmpty())
                {
                    targetObject = constructor.newInstance() as? U
                    if(targetObject != null) break
                }
            }

            if(targetObject == null) return null

            val toJsonObject = element as JsonObject
            for(field in targetObject.getSerializableEntityFields())
            {
                val refValue = toJsonObject.get(field.name)
                if(refValue == null) {
                    messageHandler.sendMessage("The variable '${field.name}'[$refValue] was invalid value that compare with base class.")
                    continue
                }
                field.set(targetObject, refValue)
            }
            return targetObject
        }

        fun <E : EntityUnit<E>> getEntity0(objectData: Any, refClazz : Class<E>): E?
        {
            try {
                val collection = EntityUnitCollection.getEntityCollection(refClazz) ?: return null
                val registerEntities = collection.getEntities() ?: return null
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
                if(entity::class.java.isAssignableFrom(k.getPersistentClass())) {
                    if(! k.isEnabled()) {
                        // Hook the reference collection.
                        var eField = entity::class.java.superclass.getDeclaredField("eCollection")
                        eField.isAccessible = true
                        eField.set(entity, k)

                        // Generate the unique signature if the entity have no id.
                        eField = entity::class.java.superclass.getDeclaredField("uuid")
                        eField.isAccessible = true
                        val uuid = eField.get(entity) as? String
                        if(uuid == null)
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

    @SafetyExecutable(libname = "Chadow.Internal.Core") private external fun onChangeHandler0(value0 : String) : String

    @SafetyExecutable(libname = "Chadow.Internal.Core") private external fun hookChangeFileInfo(data0 : String) : Boolean
}
