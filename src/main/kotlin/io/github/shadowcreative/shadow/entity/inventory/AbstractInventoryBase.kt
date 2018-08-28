package io.github.shadowcreative.shadow.entity.inventory

import org.bukkit.inventory.Inventory

interface AbstractInventoryBase
{
    fun getInventoryBase() : Inventory?

    fun getSlotComponents() : Map<Int, InventoryComponent?>
}