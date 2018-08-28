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
@file:Suppress("unused")
package io.github.shadowcreative.shadow.component

import io.github.shadowcreative.shadow.command.TextHandler
import io.github.shadowcreative.shadow.plugin.IntegratedPlugin
import io.github.shadowcreative.shadow.util.CommandUtility
import io.github.shadowcreative.shadow.util.RuskitLogger
import io.github.shadowcreative.shadow.util.StringUtility
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class FormatDescription : Cloneable
{
    @Suppress("FunctionName")
    companion object
    {
        fun DefaultDescription() : FormatDescription
        {
            val f = FormatDescription("$0")
            val lists = arrayOf("Hello {user}!",
                    "&cThe server admin didn't edited this detail message.",
                    "&fFor more descriptions, Contract them.").asList()
            f.setDescriptionSelector(0, "No Description.", lists)
            return f
        }
    }

    @Transient private val clickEventList : HashMap<Int, TextHandler<ClickEvent>> = HashMap()
    fun setClickEvent(index : Int, event : ClickEvent)
    {
        this.clickEventList[index] = TextHandler(event)
    }

    fun getClickEventList() : HashMap<Int, TextHandler<ClickEvent>> = this.clickEventList

    @Transient private val hoverEventList : HashMap<Int, TextHandler<HoverEvent>> = HashMap()
    fun setHoverEvent(index : Int, event : HoverEvent)
    {
        this.hoverEventList[index] = TextHandler(event)
    }

    fun getHoverEventList() : HashMap<Int, TextHandler<HoverEvent>> = this.hoverEventList

    /**
     * It sames show text of hoverEvent.
     */
    @Transient val selectorList : HashMap<Int, Pair<String, List<String>>> = HashMap()

    fun getDescriptionSelector() :  HashMap<Int, Pair<String, List<String>>> = this.selectorList

    fun setDescriptionSelector(index : Int, s: String, list: List<String>) { this.selectorList[index] = s to list }
    fun setDescriptionSelector(index: Int, keyword: String, desc: String) { this.selectorList[index] = keyword to arrayListOf(desc) }

    fun setDescriptionSelector(index: Int, keyword: String, vararg desc: String) { this.selectorList[index] = keyword to desc.toList() }

    fun setDescriptionSelector(index: Int, keyword: () -> String, desc : String) { this.selectorList[index] = keyword() to arrayListOf(desc) }
    fun setDescriptionSelector(index : Int, pair : Pair<String, List<String>>) { this.selectorList[index] = pair }

    private var format : String = "No description."
    fun getFormat() : String = this.format
    fun setFormat(message : String) { this.format = message }

    private var filterData : HashMap<String, String> = HashMap()
    fun getFilter() : HashMap<String, String> = this.filterData
    fun addFilter(key : String, value : String) { this.filterData[key] = value }

    public override fun clone(): Any {
        return super.clone()
    }

    fun apply(clone : Boolean = false) : FormatDescription
    {
        val fDescription = if(clone) {
            this.clone() as FormatDescription
        }
        else
        {
            this
        }
        for(k in fDescription.getFilter().keys)
        {
            val pattern = Pattern.compile("(\\{\\b$k})")
            val match = pattern.matcher(fDescription.getFormat())
            match.find()
            if(match.groupCount() != 0)
            {
                val value : String = fDescription.getFilter()[k]!!
                fDescription.setFormat(fDescription.getFormat().replace("\\{\\b$k}".toRegex(), value))
            }
        }
        return fDescription
    }

    constructor()
    {
        val dsd = DefaultDescription()
        this.format = dsd.getFormat()
    }

    constructor(bytes : ByteArray)
    {
        val str = String(bytes)
        this.format = str
    }

    constructor(c : CharArray) { this.format = String(c) }

    constructor(c : Char) { this.format = c.toString() }

    constructor(s : String) { this.format = s }

    @Suppress("UNCHECKED_CAST")
    fun send(sender : CommandSender)
    {
        if(sender is Player)
        {
            val array : Array<BaseComponent> = CommandUtility.toBaseComponent(this) as Array<BaseComponent>
            sender.spigot().sendMessage(*array)
        }
        else
        {
            throw RuntimeException("Not implemented")
        }
    }

    fun append(a : String) {
        this.format += a
    }

    fun appendFront(a : String) {
        this.format = a + this.format
    }

    fun format(f : String) {
        this.format = f
    }

    fun rawMessage() : String
    {
        val stringArray : ArrayList<String> = ArrayList()
        for((str, _) in this.selectorList.values)
        {
            stringArray.add(str)
        }
        return StringUtility.WithIndex(this.format, stringArray)
    }

    private val run : Boolean = false

    operator fun plus(to : String) : FormatDescription { this.format(this.format + to); return this }
    operator fun plus(f : FormatDescription) : FormatDescription
    {
        val messageHandler = IntegratedPlugin.CorePlugin!!.getMessageHandler()
        for((indexSelection, pair) in f.selectorList)
            f.selectorList[indexSelection] = pair

        for((indexSelection, pair) in this.selectorList)
        {
            if(f.selectorList.containsKey(indexSelection))
                messageHandler.defaultMessage("&eOperator warning: Duplicated value of index selection: $$indexSelection|$pair", RuskitLogger.Level.WARNING)
            f.selectorList[indexSelection] = pair
        }

        // Combine the event.
        f.hoverEventList.putAll(this.hoverEventList)
        f.clickEventList.putAll(this.clickEventList)

        f.format(f.format + this.format)
        return this
    }
}
