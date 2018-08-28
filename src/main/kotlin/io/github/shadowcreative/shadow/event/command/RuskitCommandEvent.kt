package io.github.shadowcreative.shadow.event.command

import io.github.shadowcreative.shadow.command.RuskitCommand
import io.github.shadowcreative.shadow.event.AbstractEvent
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
