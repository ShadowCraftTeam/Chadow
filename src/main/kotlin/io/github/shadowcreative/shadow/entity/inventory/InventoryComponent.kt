package io.github.shadowcreative.shadow.entity.inventory

import com.google.gson.*
import io.github.shadowcreative.shadow.component.JsonCompatibleSerializer
import io.github.shadowcreative.shadow.util.StringUtility
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

open class InventoryComponent : JsonCompatibleSerializer<InventoryComponent>()
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): InventoryComponent
    {
        val jsonObject = json as JsonObject
        val icObject = InventoryComponent()
        icObject.material = Material.getMaterial(jsonObject.get("value").asString)
        if(jsonObject.get("amount") == null)
            icObject.amount = 1
        else
            icObject.amount = jsonObject.get("amount").asInt

        if(jsonObject.get("damage") == null)
            icObject.damaged = 0
        else
            icObject.damaged = jsonObject.get("damage").asShort

        icObject.itemStack = ItemStack(icObject.material, icObject.amount, icObject.damaged)
        return icObject
    }

    override fun serialize(src: InventoryComponent?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        val jsonObject = JsonObject()
        val materialObject = JsonObject()
        val descriptionArray = JsonArray()
        materialObject.addProperty("value", this.material.toString())
        materialObject.addProperty("glow", this.isGlow)
        materialObject.addProperty("amount", this.amount)
        materialObject.addProperty("name", this.materialName)

        for(desc in this.description)
            descriptionArray.add(desc)
        materialObject.add("description", descriptionArray)
        return jsonObject
    }

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

    private var hoverFunction : ((Player) -> Boolean)? = null
    fun setHover(function : (Player) -> Boolean) { this.hoverFunction = function }

    private var clickFunction : ((Player) -> Boolean)? = null
    fun setClick(function : (Player) -> Boolean) { this.clickFunction = function }
    fun getClickFunction() : ((Player) -> Boolean)? = this.clickFunction

    companion object {
        fun getSlot(x : Int, y: Int) : Int = (x-1) + (9 * (y - 1))
    }
}