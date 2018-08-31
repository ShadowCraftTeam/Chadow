package io.github.shadowcreative.chadow.event.command

import io.github.shadowcreative.chadow.command.RuskitCommand
import io.github.shadowcreative.chadow.event.AbstractEvent
import org.bukkit.command.CommandSender
import org.bukkit.event.HandlerList

open class RuskitCommandEvent(var sender : CommandSender, var vcommand : RuskitCommand<*>, val argv : List<String>, var handleInstance : Any?) : AbstractEvent()
{
    companion object { private val handler = HandlerList()
        @JvmStatic
        fun getHandlerList() : HandlerList = handler
    }
    override fun getHandlers(): HandlerList = handler
}
