package io.github.shadowcreative.shadow.event.inventory

import io.github.shadowcreative.shadow.entity.AbstractInventory
import io.github.shadowcreative.shadow.event.AbstractEvent
import org.bukkit.event.HandlerList

abstract class AbstractInventoryEvent(var inventory : AbstractInventory) : AbstractEvent()
{
    companion object { private val handler = HandlerList()
        @JvmStatic
        fun getHandlerList() : HandlerList = handler
    }

    override fun getHandlers(): HandlerList = handler
}

