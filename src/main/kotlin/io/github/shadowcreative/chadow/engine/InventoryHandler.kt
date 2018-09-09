package io.github.shadowcreative.chadow.engine

import io.github.shadowcreative.chadow.entity.AbstractInventory
import io.github.shadowcreative.chadow.event.inventory.AbstractInventoryClickEvent
import io.github.shadowcreative.eunit.EntityUnitCollection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryHandler : RuntimeTaskScheduler()
{
    companion object {
        private val instance : InventoryHandler = InventoryHandler()
        @JvmStatic fun getInstance() : InventoryHandler = instance
    }

    override fun onInit(handleInstance: Any?): Any?
    {
        // This engine only use for event handler, not loop-range workstation.
        this.setActivationTask(false)
        return true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventory(e : InventoryClickEvent)
    {
        val entities = EntityUnitCollection.getEntityCollection(AbstractInventory::class.java)
        if(entities != null) {
            for (value in entities.getEntities()!!.iterator()) {
                val ref = value as AbstractInventory
                if(ref.getInventoryBase() != null && e.inventory == ref.getInventoryBase()) {
                    val event = AbstractInventoryClickEvent(ref, e.whoClicked as Player, e.slot, ref.getSlotComponents()[e.slot])
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
