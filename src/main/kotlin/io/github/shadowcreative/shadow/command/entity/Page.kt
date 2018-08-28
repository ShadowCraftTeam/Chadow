@file:Suppress("UNCHECKED_CAST")
package io.github.shadowcreative.shadow.command.entity

import io.github.shadowcreative.shadow.Executable
import io.github.shadowcreative.shadow.component.FormatDescription
import io.github.shadowcreative.shadow.util.CommandUtility
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

typealias ComponentString = Array<BaseComponent>
class Page : Executable<CommandSender>
{
    override fun execute(target: CommandSender, argv: ArrayList<String>) : Any?
    {
        val page = this.create()
        if(target is ConsoleCommandSender || target is Player)
        {
            for(message in page.baseList)
                target.spigot().sendMessage(*message)
        }
        return page
    }

    private val defineValues : HashMap<String, String> = HashMap()
    fun addDefineValue(key : String, value : String) { this.defineValues[key] = value }

    private var list : ArrayList<FormatDescription> = ArrayList()
    fun addText(s : String) { this.list.add(FormatDescription(s)) }
    fun addText(f : FormatDescription) { this.list.add(f) }

    @Synchronized
    fun create() : Page
    {
        for(k in this.list)
            this.baseList.add(CommandUtility.toBaseComponent(k) as ComponentString)
        return this
    }


    private var baseList : ArrayList<ComponentString> = ArrayList()

    constructor()

    constructor(fd : Collection<FormatDescription>) { this.list.addAll(fd) }

    constructor(l: ArrayList<ComponentString>) { this.baseList = l }
}