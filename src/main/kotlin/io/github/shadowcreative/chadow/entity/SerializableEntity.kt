package io.github.shadowcreative.chadow.entity

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.gson.JsonObject

import io.github.shadowcreative.chadow.component.JsonCompatibleSerializer
import io.github.shadowcreative.chadow.config.SynchronizeReader
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin

import java.io.File
import java.util.*

abstract class SerializableEntity<E>(entityName : String) : SynchronizeReader<E>(File(entityName))
{
    private var jsonObject : JsonObject? = null
    private val serializableComponent : Multimap<Class<out SerializableEntity<*>>, JsonCompatibleSerializer<*>> = ArrayListMultimap.create()

    companion object
    {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified E> registerEntities(plugin: IntegratedPlugin?) : MutableList<E>?
        {
            val l = ArrayList<E>()
            val target: Iterator<SynchronizeReader<*>>? = if(plugin == null) SynchronizeReader.RegisterHandledReader().values().iterator()
            else SynchronizeReader.RegisterHandledReader()[plugin].iterator()
            for(value in target!!) {
                if(E::class.java.isAssignableFrom(value::class.java)) l.add(value as E)
            }
            return l
        }
    }
}
