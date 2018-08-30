package io.github.shadowcreative.shadow.entity.coll

import com.google.common.collect.ArrayListMultimap
import io.github.shadowcreative.shadow.Handle
import io.github.shadowcreative.shadow.engine.RuskitThread
import io.github.shadowcreative.shadow.entity.EntityObject
import io.github.shadowcreative.shadow.plugin.IntegratedPlugin

open class ECollection<E : EntityObject<E>> : RuskitThread, Handle
{
    private constructor()
    {

    }

    protected constructor(uuid : String) : super()
    {

    }

    private var instancePlugin : IntegratedPlugin? = null
    fun getInstancePlugin() : IntegratedPlugin? = this.instancePlugin

    private var entityCollection : MutableList<E>? = null
    fun getEntities() : MutableList<E>? = this.entityCollection

    override fun onInit(handleInstance: Any?): Any?
    {
        return true
    }

    open fun getEntity(objectData: Any?) : E?
    {
        if(objectData == null) return null
        return null
        //return ECollection.getEntity0<E>(objectData)
    }

    companion object
    {
        private val pluginCollections : ArrayListMultimap<IntegratedPlugin, ECollection<*>> = ArrayListMultimap.create()

        private inline fun <reified E> getEntity0(objectData : Any) : E?
        {
            return null
        }
    }
}
