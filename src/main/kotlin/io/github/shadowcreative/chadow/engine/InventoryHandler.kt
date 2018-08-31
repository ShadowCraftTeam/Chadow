package io.github.shadowcreative.chadow.engine

import io.github.shadowcreative.chadow.entity.AbstractInventory
import io.github.shadowcreative.chadow.entity.SerializableEntity
import io.github.shadowcreative.chadow.event.inventory.AbstractInventoryClickEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryHandler : RuskitThread()
{
    companion object {
        private val instance : InventoryHandler = InventoryHandler()
        @JvmStatic fun getInstance() : InventoryHandler = instance
    }

    override fun onInit(handleInstance: Any?): Any?
    {
        // This engine only for event.
        this.setActivationTask(false)
        return true
    }

    @EventHandler
    fun onInventory(e : InventoryClickEvent)
    {
        val entities = SerializableEntity.registerEntities<AbstractInventory>(null)
        if(entities != null) {
            for (value in entities.iterator()) {
                if(value.getInventoryBase() != null && e.inventory == value.getInventoryBase()) {
                    val event = AbstractInventoryClickEvent(value, e.whoClicked as Player, e.slot, value.getSlotComponents()[e.slot])
                        event.run()
                    if(event.isCancelled)
                        e.isCancelled = true
                }
                else continue
            }
        }
        else return
    }
}
