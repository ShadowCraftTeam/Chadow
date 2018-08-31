package io.github.shadowcreative.chadow.event

import io.github.shadowcreative.chadow.command.TextHandler
import org.bukkit.event.HandlerList

class TextHandlerEvent(var textHandler : TextHandler<*>) : AbstractEvent()
{
    companion object { private val handler = HandlerList()
        @JvmStatic
        fun getHandlerList() : HandlerList = handler
    }
    override fun getHandlers(): HandlerList = handler
}