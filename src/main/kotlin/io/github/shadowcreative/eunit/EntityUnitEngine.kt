package io.github.shadowcreative.eunit

import io.github.shadowcreative.chadow.engine.RuskitThread
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import java.util.concurrent.ConcurrentHashMap

class EntityUnitEngine : RuskitThread()
{
    private val engine : ConcurrentHashMap<IntegratedPlugin, EntityUnit<*>> = ConcurrentHashMap()

    override fun onInit(handleInstance: Any?): Any?
    {
        val map = EntityUnitCollection.getEntityCollections()
        for(plugin in map.keySet()) {
            for(value in map[plugin])
            {

            }
        }
        return true
    }
}