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
@file:JvmName("MessageHandler")
@file:Suppress("unused", "UNUSED_PARAMETER")

package io.github.shadowcreative.chadow

import io.github.shadowcreative.chadow.component.Prefix
import io.github.shadowcreative.chadow.util.ChadowLogger
import io.github.shadowcreative.chadow.util.StringUtility
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

open class MessageHandler
{
    private var prefix : Prefix? = null
    private var hasPrefix : Boolean = false
    private var customFilter : HashMap<String, Any> = HashMap()
    private var formatFilter : HashMap<String, String> = HashMap()

    fun sendConfigMessage(key : String) : String
    {
        return key
    }

    fun sendConfigMessage(key : String, who: CommandSender) : String
    {
        return key
    }



    fun addFilter(sentence : String, replaced : String)
    {
        this.formatFilter[sentence] = replaced
    }

    fun getFilterValue(sentence: String? = null) : String?
    {
        return this.formatFilter[sentence]
    }

    fun getEntireFilter() : Map<String, String>
    {
        return this.formatFilter
    }

    private var sender : CommandSender? = null

    constructor(prefix : String)
    {
        this.prefix = Prefix(prefix)
    }

    constructor(prefix : Prefix)
    {
        this.prefix = prefix
    }

    constructor(target : CommandSender)
    {
        this.sender = target
    }

    constructor(target : CommandSender, prefix : Prefix)
    {
        this.sender = target
        this.prefix = prefix
    }

    fun defaultMessage(str : String) {
        this.sendMessage(str, this.prefix != null, ChadowLogger.DefaultLogger, true, this.formatFilter, ChadowLogger.Level.INFO)
    }

    fun defaultMessage(str : String, level : ChadowLogger.Level) {
        this.sendMessage(str, this.prefix != null, ChadowLogger.DefaultLogger, true, this.formatFilter, level)
    }

    fun sendMessage(str : String)
    {

    }

    fun sendMessage(str : String, hasPrefix : Boolean = this.hasPrefix)
    {

    }

    fun sendMessage(str : String, hasPrefix : Boolean = this.hasPrefix, logging : ChadowLogger? = null)
    {

    }

    fun sendMessage(str : String, hasPrefix : Boolean = this.hasPrefix, logging : ChadowLogger? = null, colorable: Boolean = true) {

    }

    fun sendMessage(str : String, hasPrefix : Boolean = this.hasPrefix, logging : ChadowLogger? = null,
                    colorable: Boolean = true, formatFilter : Map<String, String> = this.formatFilter)
    {

    }

    open fun sendMessage(str : String, hasPrefix : Boolean = this.hasPrefix, logging : ChadowLogger? = null,
                         colorable: Boolean = true, formatFilter : Map<String, String> = this.formatFilter, level : ChadowLogger.Level = ChadowLogger.Level.INFO)
    {
        var message = str
        if(hasPrefix)
        {
            message = this.prefix!!.getNameWithAttach() + " " + message
        }

        message = StringUtility.color(message)
        if(! colorable)
        {
            message = ChatColor.stripColor(message)
        }

        sender!!.sendMessage(StringUtility.color(message))
    }

    fun defaultMessage(str : String,
                       target: CommandSender = this.sender!!,
                       level : ChadowLogger.Level = ChadowLogger.Level.INFO) {
        this.sendTargetMessage(str, target, this.prefix != null, ChadowLogger.DefaultLogger, true, this.formatFilter, level)
    }

    open fun sendTargetMessage(str : String,
                               senderTarget : CommandSender,
                               hasPrefix : Boolean = this.hasPrefix,
                               logging : ChadowLogger? = null,
                               colorable: Boolean = true,
                               formatFilter : Map<String, String>? = this.formatFilter,
                               level : ChadowLogger.Level = ChadowLogger.Level.INFO) {

        var prefixMessage = if(hasPrefix) this.prefix!!.getNameWithAttach() + " " else ""
        var message = str

        if(formatFilter != null)
        {
            message = StringUtility.WithIndex(message, formatFilter)
        }

        if(colorable)
            message = StringUtility.color(message)
            prefixMessage = StringUtility.color(prefixMessage)

        senderTarget.sendMessage("$prefixMessage$message")
    }
}