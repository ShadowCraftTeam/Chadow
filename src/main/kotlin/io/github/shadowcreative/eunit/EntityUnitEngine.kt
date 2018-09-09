package io.github.shadowcreative.eunit

import io.github.shadowcreative.chadow.engine.RuntimeTaskScheduler

class EntityUnitEngine : RuntimeTaskScheduler()
{
    override fun onInit(handleInstance: Any?): Any? {
        for(es in EntityUnitCollection.getEntityCollections().get(this.activePlugin)) {
            for(e in es.getEntities()!!.iterator()) {
                if(! e.isEnabled()) e.setEnabled(es.isEnabled())
            }
        }
        return true
    }
}