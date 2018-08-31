package io.github.shadowcreative.chadow.entity.inventory

import org.bukkit.inventory.Inventory

interface AbstractInventoryBase
{
    fun getInventoryBase() : Inventory?

    fun getSlotComponents() : Map<Int, InventoryComponent?>
}