package io.github.shadowcreative.chadow.entity

import com.google.common.collect.ArrayListMultimap
import com.google.gson.JsonElement
import io.github.shadowcreative.chadow.Activator
import io.github.shadowcreative.chadow.component.Internal
import io.github.shadowcreative.chadow.platform.GenericInstance
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import io.github.shadowcreative.chadow.sendbox.SafetyExecutable
import java.util.*

open class ECollection<E : Entity<E>> : GenericInstance<E>, Activator<IntegratedPlugin>
{
    @Synchronized
    private fun onChangeHandler(targetClazz : Class<E>? = this.getPersistentClass()) : Map<String, Boolean>? {
        if(targetClazz == null)
            return null

        val result = this.onChangeHandler0(targetClazz.typeName)
        return null
    }

    @SafetyExecutable(libname = "Chadow.Internal.Core")
    private external fun onChangeHandler0(value0 : String) : String

    override fun isEnabled(): Boolean {
        return this.instancePlugin != null
    }

    override fun setEnabled(handleInstance: IntegratedPlugin)
    {
        this.instancePlugin = handleInstance
        this.setEnabled(this.instancePlugin != null)
    }

    override fun setEnabled(active: Boolean)
    {
        if(active)
        {

        }
        else
        {

        }
    }

    fun registerObject(entity: Entity<E>, objectId: String): Boolean
    {
        var handlePlugin = this.instancePlugin
        if(this.instancePlugin == null)
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

    fun gerenate() : ECollection<E>
    {
        ECollection.pluginCollections.put(this.instancePlugin, this)
        return this
    }


    protected fun setIdentifiableObject(vararg fieldString : String) {
        this.identifier.addAll(fieldString)
    }

    private val uuid : String
    fun getUniqueId() : String = this.uuid

    @Internal
    private var instancePlugin : IntegratedPlugin? = null
    fun getPlugin() : IntegratedPlugin? = this.instancePlugin

    @Internal
    private var entityCollection : MutableList<Entity<E>>? = null
    fun getEntities() : MutableList<Entity<E>>? = this.entityCollection

    @Internal
    private val identifier : MutableList<String> = ArrayList()
    fun getIdentifier() : MutableList<String> = this.identifier

    open fun getEntity(objectData: Any?) : E?
    {
        if(objectData == null) return null
        @Suppress("UNCHECKED_CAST")
        return ECollection.getEntity0(objectData) as? E?
    }

    fun getEntityObject(objectData : Any?) : E?
    {
        return null
    }

    companion object
    {
        private val pluginCollections : ArrayListMultimap<IntegratedPlugin, ECollection<*>> = ArrayListMultimap.create()
        fun getECollections() : ArrayListMultimap<IntegratedPlugin, ECollection<*>> = this.pluginCollections
        private fun getEntity0(objectData: Any): Any?
        {
            for(k in pluginCollections.values())
            {

            }
            return null
        }

        fun <U> deserialize(element : JsonElement, reference: Class<*>) : U?
        {
            return null
        }


        fun asReference(entity: Entity<*>)
        {
            for(k in ECollection.getECollections().values())
            {
                if(k.getPersistentClass() == entity::class.java)
                {
                    if(! k.isEnabled()) {
                        var eField = entity::class.java.superclass.getDeclaredField("eCollection")
                        eField.isAccessible = true
                        eField.set(entity, k)
                        eField = entity::class.java.superclass.getDeclaredField("uuid")
                        eField.isAccessible = true
                        eField.set(entity, UUID.randomUUID().toString().replace("-", ""))
                        return
                    }
                    else
                    {
                        // The ECollection was disabled, It couldn't register your entity.
                        return
                    }
                }
            }
        }
    }
}
