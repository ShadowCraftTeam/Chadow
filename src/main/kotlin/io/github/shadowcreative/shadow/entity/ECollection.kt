package io.github.shadowcreative.chadow.entity

import com.google.common.collect.ArrayListMultimap
import com.google.gson.JsonElement
import io.github.shadowcreative.shadow.Activator
import io.github.shadowcreative.shadow.platform.GenericInstance
import io.github.shadowcreative.shadow.plugin.IntegratedPlugin
import java.util.*

open class ECollection<E : Entity<E>> : GenericInstance<E>, Activator<ECollection<E>>
{
    override fun onInit(handleInstance: Any?): Any?
    {
        return super.onInit(this)
    }

    override fun isEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setEnabled(handleInstance: ECollection<E>)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setEnabled(active: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    protected constructor()
    {
        @Suppress("LeakingThis")
        ECollection.getECollections().put(null, this)
        this.entityCollection = ArrayList()
    }

    protected constructor(uuid : String) : super()
    {
        this.entityCollection = ArrayList()
    }

    protected fun setIdentifiableObject(vararg fieldString : String) {
        this.identifier.addAll(fieldString)
    }

    private var instancePlugin : IntegratedPlugin? = null
    fun getPlugin() : IntegratedPlugin? = this.instancePlugin

    private var entityCollection : MutableList<Entity<E>>? = null
    fun getEntities() : MutableList<Entity<E>>? = this.entityCollection

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
                    var eField = entity::class.java.superclass.getDeclaredField("eCollection")
                    eField.isAccessible = true
                    eField.set(entity, k)
                    eField = entity::class.java.superclass.getDeclaredField("uuid")
                    eField.isAccessible = true
                    eField.set(entity, UUID.randomUUID().toString().replace("-", ""))
                    return
                }
            }
        }
    }
}
