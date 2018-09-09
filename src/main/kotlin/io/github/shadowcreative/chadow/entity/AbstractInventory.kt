package io.github.shadowcreative.chadow.entity

import io.github.shadowcreative.chadow.entity.inventory.AbstractInventoryBase
import io.github.shadowcreative.chadow.entity.inventory.InventoryComponent
import io.github.shadowcreative.eunit.EntityUnit
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.util.*
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

abstract class AbstractInventory : EntityUnit<AbstractInventory>, AbstractInventoryBase
{
    private constructor() : super(UUID.randomUUID().toString())

    protected constructor(name: String) : super(UUID.randomUUID().toString()) {
        this.customizationName = name
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

    private var customizationName : String = "Undefined"
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

object InventoryUtil {

    private var version: String? = null

    private val iInventoryField: Field

    private val titleField: Field

    private val handleField: Field

    private val containerCounterField: Field

    private val openWindowPacketConstructor: Constructor<*>

    private val playerConnectionField: Field

    private val sendPacket: Method

    init
    {
        val parts = Bukkit::class.java.name.split(".".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        version = if (parts.size == 4) ""
        else "." + parts[4]
        var tiinv: Field? = null
        var ttitle: Field? = null
        var thandle: Field? = null
        var tContainerCounter: Field? = null
        var tOpenWindowPacket: Constructor<*>? = null
        var tPlayerConnection: Field? = null
        val tSendPacket: Method
        try {
            tiinv = getNMSVersionClass("org.bukkit.craftbukkit.inventory.CraftInventory").getDeclaredField("inventory")
            tiinv!!.isAccessible = true

            ttitle = getNMSVersionClass("org.bukkit.craftbukkit.inventory.CraftInventoryCustom\$MinecraftInventory").getDeclaredField("title")
            ttitle!!.isAccessible = true

            thandle = getNMSVersionClass("org.bukkit.craftbukkit.entity.CraftEntity").getDeclaredField("handle")
            thandle!!.isAccessible = true

            tContainerCounter = getNMSVersionClass("net.minecraft.server.EntityPlayer").getDeclaredField("containerCounter")
            tContainerCounter!!.isAccessible = true

            thandle = getNMSVersionClass("org.bukkit.craftbukkit.entity.CraftEntity").getDeclaredField("handle")
            thandle!!.isAccessible = true

            tOpenWindowPacket = getNMSVersionClass("net.minecraft.server.PacketPlayOutOpenWindow").
                    getDeclaredConstructor(Int::class.javaPrimitiveType, Int::class.javaPrimitiveType,
                            String::class.java, Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)

            tOpenWindowPacket!!.isAccessible = true

            tPlayerConnection = getNMSVersionClass("net.minecraft.server.EntityPlayer").getDeclaredField("playerConnection")
            tPlayerConnection!!.isAccessible = true

            tSendPacket = getNMSVersionClass("net.minecraft.server.PlayerConnection").
                    getDeclaredMethod("sendPacket", tOpenWindowPacket.declaringClass.superclass)
            tSendPacket.isAccessible = true

        }
        // Any would do, regardless
        catch (ex: Exception) {
            throw ExceptionInInitializerError(ex)
        }

        iInventoryField = tiinv
        titleField = ttitle
        handleField = thandle
        containerCounterField = tContainerCounter
        openWindowPacketConstructor = tOpenWindowPacket
        playerConnectionField = tPlayerConnection
        sendPacket = tSendPacket
    }

    @Throws(ClassNotFoundException::class)
    private fun getNMSVersionClass(className: String): Class<*> {
        if (className.startsWith("net.minecraft.server"))
            return if (version!!.isEmpty())
                Class.forName(className)
            else
                Class.forName(String.format("net.minecraft.server%s.%s", version, className.substring("net.minecraft.server.".length)))
        else if (className.startsWith("org.bukkit.craftbukkit"))
            return if (version!!.isEmpty())
                Class.forName(className)
            else
                Class.forName(String.format("net.minecraft.server%s.%s", version, className.substring("org.bukkit.craftbukkit.".length)))
        throw IllegalArgumentException("Not a versioned class!")
    }

    fun renameInventory(player: Player, inventory: Inventory, title: String) {
        try {
            val iinv = iInventoryField.get(inventory)
            titleField.set(iinv, title)
            val handle = handleField.get(player)
            val containerCounter = containerCounterField.get(handle) as Int
            val playerConnection = playerConnectionField.get(handle)
            val packet = openWindowPacketConstructor.newInstance(containerCounter, 0, title, inventory.size, false)
            sendPacket.invoke(playerConnection, packet)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}