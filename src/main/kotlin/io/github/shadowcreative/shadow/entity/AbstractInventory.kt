package io.github.shadowcreative.shadow.entity

import io.github.shadowcreative.shadow.entity.inventory.AbstractInventoryBase
import io.github.shadowcreative.shadow.entity.inventory.InventoryComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.util.*

abstract class AbstractInventory : SerializableEntity<AbstractInventory>, AbstractInventoryBase
{
    private constructor() : super(UUID.randomUUID().toString())

    protected constructor(inventoryName: String) : super(inventoryName)
    {
        this.customizationName = inventoryName
    }

    protected open fun initialize(tableRows: Int, inventoryName: String = this.customizationName, clickSound: SoundEffect? = null, owner: Player? = null) {
        initialize(this, tableRows, inventoryName, clickSound, owner)
    }

    companion object
    {
        fun initialize(entity : AbstractInventory,
                       tableRows: Int = 1,
                       inventoryName: String,
                       clickSound: SoundEffect? = null,
                       owner: Player? = null) : AbstractInventory {
            val targetObject : AbstractInventory = entity
            for (i in 0..tableRows * 9) {
                val component = InventoryComponent()
                targetObject.slotComponents[i] = component
                targetObject.soundEffects[i] = clickSound }
            targetObject.owner = owner
            targetObject.inventoryBase = Bukkit.createInventory(owner, tableRows * 9, inventoryName)
            return targetObject
        }
    }

    open fun open() { (owner!! as Player).openInventory(this.inventoryBase) }

    open fun open(p : Player) { p.openInventory(this.inventoryBase) }

    private var owner : InventoryHolder? = null
    fun getOwner() : InventoryHolder? = this.owner!!

    private var inventoryBase : Inventory? = null
    override fun getInventoryBase() : Inventory? = this.inventoryBase

    private var customizationName : String = "Custom Inventory"
    fun getInventoryName() = this.customizationName

    private val soundEffects : HashMap<Int, SoundEffect?> = HashMap()
    fun getSoundEffects() : HashMap<Int, SoundEffect?> = this.soundEffects
    fun hasSoundEffects(slot : Int) : Boolean = this.soundEffects[slot] != null

    private val slotComponents : HashMap<Int, InventoryComponent?> = HashMap()
    override fun getSlotComponents() : Map<Int, InventoryComponent?> = this.slotComponents

    fun setComponent(slot : Int, component: InventoryComponent) {
        this.slotComponents[slot] = component
    }
}