package io.github.shadowcreative.shadow.event

import io.github.shadowcreative.shadow.command.TextHandler
import org.bukkit.event.HandlerList

class TextHandlerEvent(var textHandler : TextHandler<*>) : AbstractEvent()
{
    companion object { private val handler = HandlerList()
        @JvmStatic
        fun getHandlerList() : HandlerList = handler
    }
    override fun getHandlers(): HandlerList = handler
}