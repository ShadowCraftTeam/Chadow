package io.github.shadowcreative.chadow.event.inventory

import io.github.shadowcreative.chadow.entity.AbstractInventory
import io.github.shadowcreative.chadow.entity.inventory.InventoryComponent
import org.bukkit.entity.Player

class AbstractInventoryClickEvent(inventory : AbstractInventory, var executor : Player, var slot : Int, var clicked : InventoryComponent?) : AbstractInventoryEvent(inventory)
