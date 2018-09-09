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
package io.github.shadowcreative.chadow.util

import io.github.shadowcreative.chadow.command.ChadowCommand
import io.github.shadowcreative.chadow.command.entity.ComponentString
import io.github.shadowcreative.chadow.command.misc.CommandOrder
import io.github.shadowcreative.chadow.component.FormatDescription
import io.github.shadowcreative.chadow.component.Helper
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import java.util.*

object CommandUtility
{
    @Suppress("MemberVisibilityCanBePrivate")
    fun stringWithEscape(s : List<String>) : String
    {
        var string = ""
        for((i, str) in s.withIndex())
        {
            string += str
            if(s.size != i + 1)
            {
                string += "\n"
            }
        }
        return StringUtility.color(string)
    }

    /**
     * Sorts the child command or the specified command list.
     * @param order The sort type
     * @param otherList The target list to sort
     */
    fun sortCommand(order: CommandOrder, otherList: List<ChadowCommand<*>>)
    {
        // It is a variable used to store the value of the order first.
        var const = 1

        // This constant is the Comparable anonymous function needed to sort objects.
        // The alignment is based on the alphabetical order of the main command.
        val func = fun(o1: ChadowCommand<*>?, o2: ChadowCommand<*>?): Int
        {
            val s = arrayOf(o1!!.getCommand(), o2!!.getCommand())
            Arrays.sort(s)
            return when
            {
                o1.getCommand() == s[0] -> -const
                o1.getCommand() == s[1] -> const
                o1.getCommand() == o2.getCommand() -> 0
                else -> 0
            }
        }

        when (order)
        {
        // Sort alphabetically.
            CommandOrder.ALPHABET         -> { const *= -1; Collections.sort(otherList, func) }

        // Sort alphabetically by back-order.
            CommandOrder.ALPHABET_REVERSE -> Collections.sort(otherList, func)

        // Sort randomly. This is always different for each function call.
            CommandOrder.RANDOMIZE        -> Collections.shuffle(otherList, Random(System.nanoTime()))
        }
    }

    fun toBaseComponent(fd : FormatDescription, rawType: Boolean = false, applyFill : Boolean = true) : Any?
    {
        var componentBuilder : ComponentBuilder? = null
        var selection = fd.getFormat()
        if(applyFill)
            selection = StringUtility.WithIndexString(selection, fd.getFilter())

        val indexArray = StringUtility.indexArgumentPosition(selection)
        val formatSplit = selection.split("\\{[0-9]}".toRegex()).toTypedArray()

        // If the split format is empty (= Not found current index)
        if(formatSplit.isEmpty())
            return if(rawType) componentBuilder else convertToColor(componentBuilder!!.create())
        else
        {
            val dummy : HoverEvent? = null
            for((index, value) in formatSplit.withIndex())
            {
                if(componentBuilder == null)
                    componentBuilder = ComponentBuilder(value)
                else
                    componentBuilder.append(value)

                componentBuilder.event(dummy)

                var hover : Pair<String, List<String>>? = null

                if(index + 1 != formatSplit.size)
                    hover = fd.selectorList[indexArray[index]]

                if(hover != null)
                {
                    // append value.
                    componentBuilder.append(hover.first)
                    // append with description.
                    componentBuilder.event(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(stringWithEscape(hover.second)).create()))
                }

                if (fd.getClickEventList().containsKey(index))
                    componentBuilder.event(fd.getClickEventList()[index]!!.onLoad().second)

                if (fd.getHoverEventList().containsKey(index))
                    componentBuilder.event(fd.getHoverEventList()[index]!!.onLoad().second)
            }
        }
        return if(rawType) componentBuilder else convertToColor(componentBuilder!!.create())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> convertToColor(out : T) : T?
    {
        when(out)
        {
            is Array<*> -> {
                return convertToColorArray(out as ComponentString) as T?
            }
            is ComponentBuilder -> {}
            is String -> return StringUtility.color(out) as T?
        }
        return null
    }

    fun convertToColorArray(out : Array<BaseComponent>) : ComponentString
    {
        val out0 = ArrayList<BaseComponent>()

        for(v in out) {

            val subArray = ArrayList<BaseComponent>()
            val target = v.toPlainText()

            if(Helper.containsString(target)) {
                var componentBuilder : ComponentBuilder? = null
                val hoverEvent = v.hoverEvent
                val clickEvent = v.clickEvent

                val find = target.split(("&[0-9a-z]").toRegex())
                var selected = 0

                for(sv in find) {

                    val t = target.indexOf(sv)
                    var colorcode: String?

                    colorcode = if(t == 0) {
                        target.substring(selected, selected + 2)
                    }
                    else {
                        target.substring(t - 2, t)
                    }

                    if(componentBuilder == null) componentBuilder = ComponentBuilder(sv)
                    else componentBuilder.append(sv)

                    val cc = Helper.getColorExpression(colorcode).colorType
                    applyColorCode(componentBuilder, cc)

                    componentBuilder.event(hoverEvent)
                    componentBuilder.event(clickEvent)
                    selected += sv.length + 2
                }
                subArray.addAll(componentBuilder!!.create())
            }
            else
            {
                subArray.add(v)
            }
            out0.addAll(subArray)
        }
        return out0.toTypedArray()
    }

    fun applyColorCode(componentBuilder : ComponentBuilder, cc : ChatColor)
    {
        when (cc)
        {
            ChatColor.BOLD -> componentBuilder.bold(true)

            ChatColor.ITALIC -> componentBuilder.italic(true)

            ChatColor.RESET -> componentBuilder.reset()

            ChatColor.MAGIC -> componentBuilder.obfuscated(true)

            ChatColor.UNDERLINE -> componentBuilder.underlined(true)

            ChatColor.STRIKETHROUGH -> componentBuilder.strikethrough(true)

            else -> {
                componentBuilder.color(cc)

                componentBuilder.bold(false)

                componentBuilder.italic(false)

                componentBuilder.obfuscated(false)

                componentBuilder.underlined(false)

                componentBuilder.strikethrough(false)
            }
        }
    }
}