package io.github.shadowcreative.eunit

import io.github.shadowcreative.chadow.engine.RuntimeTaskScheduler

class EntityUnitEngine : RuntimeTaskScheduler()
{
    override fun onInit(handleInstance: Any?): Any? {
        for(es in EntityUnitCollection.getEntityCollections().get(this.activePlugin)) {
            val entities = es.getEntities()
            if(entities.size == 0) continue
            for(e in entities.toList()) {

                // Maybe this entity created by initialized collection.
                // It must registers plugin information for use properly.
                if(! e.hasActivePlugin()) {
                    e.setPlugin(this.activePlugin!!)
                }

                if(! e.onDisk() && e.internalModified) { e.setEnabled(false) }
                else e.setEnabled(this.activePlugin)
            }
        }
        return true
    }
}