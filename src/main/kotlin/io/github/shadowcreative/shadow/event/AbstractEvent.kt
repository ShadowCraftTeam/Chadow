@file:Suppress("UNCHECKED_CAST")

package io.github.shadowcreative.shadow.event

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import java.util.*

abstract class AbstractEvent : Event(), Runnable, Cancellable
{
    private var cancel: Boolean = false
    private var customData: HashMap<Any, Any> = HashMap()
    fun getCustomData() : HashMap<Any, Any> = customData

    final override fun run()
    {
        Bukkit.getPluginManager().callEvent(this)
    }

    override fun isCancelled(): Boolean
    {
        return this.cancel
    }

    override fun setCancelled(cancel: Boolean)
    {
        this.cancel = cancel
    }

    fun setCustomData(m: Map<Any, Any>)
    {
        this.customData = HashMap(m)
    }

    fun insertCustomData(key : Any, value: Any)
    {
        this.customData[key] = value
    }


}