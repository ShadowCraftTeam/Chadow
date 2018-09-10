package io.github.shadowcreative.eunit

import io.github.shadowcreative.chadow.engine.RuntimeTaskScheduler

class EntityUnitEngine : RuntimeTaskScheduler()
{
    override fun onInit(handleInstance: Any?): Any? {
        for(es in EntityUnitCollection.getEntityCollections().get(this.activePlugin)) {
            for(e in es.getEntities()!!.iterator()) {
                if(! e.onDisk()) {
                    e.setEnabled(false)
                }
                else e.setEnabled(this.activePlugin)
            }
        }
        return true
    }
}