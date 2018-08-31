package io.github.shadowcreative.chadow.event.config

import io.github.shadowcreative.chadow.config.SynchronizeReader
import io.github.shadowcreative.chadow.event.AbstractEvent
import org.bukkit.event.HandlerList

open class SynchronizeReaderEvent(var target: SynchronizeReader<*>) : AbstractEvent()
{
    companion object { private val handler = HandlerList()
        @JvmStatic
        fun getHandlerList() : HandlerList = handler
    }

    override fun getHandlers(): HandlerList = handler
}
