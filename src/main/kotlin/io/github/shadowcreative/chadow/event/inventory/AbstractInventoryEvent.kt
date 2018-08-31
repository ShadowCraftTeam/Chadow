package io.github.shadowcreative.chadow.event.inventory

import io.github.shadowcreative.chadow.entity.AbstractInventory
import io.github.shadowcreative.chadow.event.AbstractEvent
import org.bukkit.event.HandlerList

abstract class AbstractInventoryEvent(var inventory : AbstractInventory) : AbstractEvent()
{
    companion object { private val handler = HandlerList()
        @JvmStatic
        fun getHandlerList() : HandlerList = handler
    }

    override fun getHandlers(): HandlerList = handler
}

