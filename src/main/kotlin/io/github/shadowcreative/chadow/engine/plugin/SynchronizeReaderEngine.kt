package io.github.shadowcreative.chadow.engine.plugin

import io.github.shadowcreative.chadow.engine.RuntimeTaskScheduler
import io.github.shadowcreative.chadow.event.config.SynchronizeReaderEvent
import org.bukkit.event.EventHandler

class SynchronizeReaderEngine : RuntimeTaskScheduler()
{
    companion object {
        private val instance : SynchronizeReaderEngine = SynchronizeReaderEngine()
        @JvmStatic fun getInstance() : SynchronizeReaderEngine = instance
    }

    override fun onInit(handleInstance: Any?): Any?
    {
        /*
        for(key in SynchronizeReader.RegisterHandledReader().keys())
            for(value in SynchronizeReader.RegisterHandledReader()[key])
                value.onInit(null)
        */
        return true
    }

    override fun preLoad(active: Boolean)
    {
        if(active)
        {

        }
        else
        {

        }
    }

    @EventHandler
    fun onChange(e : SynchronizeReaderEvent)
    {
        val lastHash: String = e.getCustomData()["lastHash"] as String
        //e.target.verify(lastHash)
    }
}
