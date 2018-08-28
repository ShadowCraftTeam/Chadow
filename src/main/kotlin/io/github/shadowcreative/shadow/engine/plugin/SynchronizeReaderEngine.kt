package io.github.shadowcreative.shadow.engine.plugin

import io.github.shadowcreative.shadow.config.SynchronizeReader
import io.github.shadowcreative.shadow.engine.RuskitThread
import io.github.shadowcreative.shadow.event.config.SynchronizeReaderEvent
import org.bukkit.event.EventHandler

class SynchronizeReaderEngine : RuskitThread()
{
    companion object {
        private val instance : SynchronizeReaderEngine = SynchronizeReaderEngine()
        @JvmStatic fun getInstance() : SynchronizeReaderEngine = instance
    }

    override fun onInit(handleInstance: Any?): Any?
    {
        for(key in SynchronizeReader.RegisterHandledReader().keys())
            for(value in SynchronizeReader.RegisterHandledReader()[key])
                value.onInit(null)
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
