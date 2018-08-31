package io.github.shadowcreative.chadow.engine

import io.github.shadowcreative.chadow.Activator
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
abstract class RuskitThread : SustainableHandler(), Listener, Activator<IntegratedPlugin?>
{
    companion object
    {
        private val registeredFramework = HashSet<RuskitThread>()
        val allFramework: Set<RuskitThread>
            get() = registeredFramework
    }

    val id     : String      = UUID.randomUUID().toString()
    var delay  : Long        = 0L;    protected set
    var period : Long        = 0L;    protected set
    var isSync : Boolean     = true;  protected set
    var task   : BukkitTask? = null;  private set
    val taskId : Int get() = if (this.task == null) -1 else this.task!!.taskId

    var activePlugin: IntegratedPlugin? = null; private set

    fun hasActivePlugin(): Boolean = this.activePlugin != null

    fun setPlugin(plugin: IntegratedPlugin)
    {
        if (this.hasActivePlugin()) return
        this.activePlugin = plugin
    }

    override fun setEnabled(handleInstance: IntegratedPlugin?)
    {
        this.activePlugin = handleInstance
        this.setEnabled(handleInstance != null)
    }

    override fun isEnabled(): Boolean
    {
        for (core in allFramework) {
            if (core == this)
            {
                return true
            }
        }
        return false
    }

    override fun setEnabled(active: Boolean)
    {
        this.preLoad(active)
        this.loadRegisterListener(active)
        this.setActivationTask(active)
        this.finLoad(active)

        if (active) {
            if (!this.isActivated()) registeredFramework.add(this)
        }
        else {
            if (this.isActivated())  registeredFramework.remove(this)
        }
    }

    override fun equals(other: Any?): Boolean
    {
        if (other == null) return false
        if (other is RuskitThread) {
            return other.task === this.task && other.activePlugin === this.activePlugin
        }
        return false
    }

    fun isActivated(): Boolean = allFramework.any { it == this }

    fun setActivationTask(active: Boolean)
    {
        if (active) {
            if (this.activePlugin!!.isEnabled)
                if (this.isSync) {
                    this.task = Bukkit.getScheduler().runTaskTimer(this.activePlugin, this, this.delay, this.period)
                }
                else {
                    this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.activePlugin, this, this.delay, this.period)
                }
        }
        else {
            if (this.task != null) {
                this.task!!.cancel()
                this.task = null
            }
        }
    }

    fun loadRegisterListener(active: Boolean)
    {
        if (active) {
            val plugin : IntegratedPlugin = this.activePlugin!!
            if (plugin.isEnabled) Bukkit.getPluginManager().registerEvents(this, this.activePlugin)
        }
        else {
            HandlerList.unregisterAll(this)
        }
    }

    protected open fun preLoad(active: Boolean) {}


    protected open fun finLoad(active: Boolean) {}

    /**
     * Call the action method synchronously.
     */
    @Synchronized fun sync() = this.run()

    override fun hashCode(): Int
    {
        var result = delay.hashCode()
        result = 31 * result + period.hashCode()
        result = 31 * result + isSync.hashCode()
        result = 31 * result + (task?.hashCode() ?: 0)
        result = 31 * result + (activePlugin?.hashCode() ?: 0)
        result = 31 * result + id.hashCode()
        return result
    }
}