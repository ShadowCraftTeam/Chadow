package io.github.shadowcreative.chadow.entity.inventory

import io.github.shadowcreative.chadow.component.Internal
import io.github.shadowcreative.chadow.util.StringUtility
import io.github.shadowcreative.eunit.EntityUnit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class InventoryComponent : EntityUnit<InventoryComponent>()
{
    private var material : Material = Material.GRASS

    private var isGlow : Boolean = false

    private var meterialData : Byte = 0

    private var materialName : String = "Default"
    fun setMaterialName(name : String) {
        this.materialName = name
        this.itemStack.itemMeta.displayName = StringUtility.color(name)
    }

    private var amount : Int = 1
    fun setAmount(value : Int) {
        this.amount = value
        this.itemStack.amount = this.amount
    }

    private var damaged : Short = 0
    fun setDamage(value : Short)
    {
        this.damaged = value
        this.itemStack.durability = (this.itemStack.durability - damaged).toShort()
    }

    @Transient
    private var itemStack : ItemStack = ItemStack(material, amount, damaged)

    @Synchronized
    fun toItemStack() : ItemStack {
        if(isGlow)
        {

        }
        return itemStack
    }

    private val description: ArrayList<String> = ArrayList()
    fun setDescription(index : Int, description : String) {
        this.description[index] = description
        this.itemStack.itemMeta.lore = StringUtility.color(this.description)
    }

    @Internal
    private var hoverFunction : ((Player) -> Boolean)? = null
    fun setHover(function : (Player) -> Boolean) { this.hoverFunction = function }

    @Internal
    private var clickFunction : ((Player) -> Boolean)? = null
    fun setClick(function : (Player) -> Boolean) { this.clickFunction = function }
    fun getClickFunction() : ((Player) -> Boolean)? = this.clickFunction

    companion object
    {
        fun getSlot(x : Int, y: Int) : Int = (x-1) + (9 * (y - 1))
    }
}