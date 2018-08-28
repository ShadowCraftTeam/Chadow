package io.github.shadowcreative.shadow.event.inventory

import io.github.shadowcreative.shadow.entity.AbstractInventory
import io.github.shadowcreative.shadow.entity.inventory.InventoryComponent
import org.bukkit.entity.Player

class AbstractInventoryClickEvent(inventory : AbstractInventory, var executor : Player, var slot : Int, var clicked : InventoryComponent?) : AbstractInventoryEvent(inventory)
