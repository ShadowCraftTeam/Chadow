/*
Copyright (c) 2018 ruskonert
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package io.github.shadowcreative.shadow.command

import io.github.shadowcreative.shadow.event.TextHandlerEvent
import io.github.shadowcreative.shadow.platform.code.TargetBuilder
import io.github.shadowcreative.shadow.plugin.IntegratedPlugin

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent

import org.bukkit.Bukkit
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin

class TextHandler<E>(private var event : E) : TargetBuilder<TextHandler<E>>(), PluginIdentifiableCommand, Runnable
{
    // Execute the code when the text handler is initialize from integrated plugin loader.
    override fun run()
    {

    }

    private var defaultPlugin : Plugin = IntegratedPlugin.CorePlugin!!
    override fun getPlugin() : Plugin = this.defaultPlugin
    fun setPlugin(p : Plugin) { this.defaultPlugin = p }

    private var actionType : ClickEvent.Action = ClickEvent.Action.SUGGEST_COMMAND
    fun getActionType() : ClickEvent.Action = this.actionType
    fun setActionType(action : ClickEvent.Action) { this.actionType = action }

    fun onLoad(repeatable : Boolean = false, handleEvent : Boolean = false, plugin : Plugin = this.defaultPlugin) : Pair<Int, E>
    {
        val taskId : Int = if(repeatable)
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, 0L).taskId else -1
        if(handleEvent)
        {
            val handlerEvent = TextHandlerEvent(this)
            if(! handlerEvent.isCancelled)
            {
                handlerEvent.run()
            }
        }
        return taskId to event
    }

    @Suppress("UNCHECKED_CAST")
    fun forceClickEvent() : Pair<Int, ClickEvent>
    {
        return this.onLoad() as Pair<Int, ClickEvent>
    }

    @Suppress("UNCHECKED_CAST")
    fun forceHoverEvent() : Pair<Int, HoverEvent>
    {
        return this.onLoad() as Pair<Int, HoverEvent>
    }
}

