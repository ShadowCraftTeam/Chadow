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
@file:Suppress("MemberVisibilityCanBePrivate")

package io.github.shadowcreative.chadow.util

import org.bukkit.ChatColor
import java.awt.Dimension
import java.awt.Toolkit
import java.util.*
import java.util.regex.Pattern

object StringUtility
{
    fun isUUID(str: String) : Boolean
    {
        return try {
            UUID.fromString(str)
            true
        }
        catch(e : Exception)
        {
            false
        }
    }


    fun color(str : String) : String = ChatColor.translateAlternateColorCodes('&', str)
    fun color(str : MutableList<String>) : MutableList<String> {
        for((index, st) in str.withIndex()) {
            str[index] = color(st)
        }
        return str
    }

    fun getScreenResolutionSize() : Dimension
    {
        val size : Dimension? =  Toolkit.getDefaultToolkit().screenSize
        return size!!
    }

    fun WithIndexString(format0 : String, args : Map<String, Any?>) : String
    {
        var format = format0
        if(args.isEmpty()) return format
        if(format.matches(".*\\{[a-zA-Z0-9!@#$%^&*()_-]+}.*".toRegex())) {
            for((select, value) in args) {
                if(format.contains("{$select}")) {
                    when(value) {
                        is Number -> {
                            format = format.replace("{$select}", value.toString())
                        }
                        is String -> {
                            format = format.replace("{$select}", value)
                        }
                        is Boolean -> {
                            format = format.replace("{$select}", value.toString())
                        }
                    }
                }
            }
        }
        return format
    }

    fun WithIndex(format0 : String, vararg args : Any?) : String
    {
        var format = format0
        if(args.isEmpty()) return format
        var i = 0
        while(format.matches(".*\\{[0-9]+}.*".toRegex()))
        {
            when(args[i])
            {
                is Number -> {
                    format = format.replace("{$i}", args[i].toString())
                }
                is String -> {
                    format = format.replace("{$i}", args[i] as String)
                }
                is Boolean -> {
                    format = format.replace("{$i}", (args[i] as Boolean).toString())
                }
                is List<*> -> {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        for((index, s) in (args[i] as List<String>).withIndex())
                            format = format.replace("{$index}", s)
                    }
                    catch(e : TypeCastException)
                    {
                        e.printStackTrace()
                    }
                }
            }
            i++
        }
        return format
    }

    private fun <E> toString(v : E) : String = v.toString()

    /* Returns screen size from external libs module. Not Implemented. */
    private external fun getScreenResolutionSize0(): Nothing

    internal fun <R> valueAssert(x : R, func : (R) -> Any?) : Boolean =  func(x) as Boolean

    fun isArgumentOption(argument: String): Boolean = argument.startsWith("-")
            || argument.startsWith("--")

    fun getArgumentOptionName(option: String): String
    {
        if(!isArgumentOption(option))
        {
            throw NullPointerException("not argument option")
        }
        else
        {
            return option.replace("-", "")
        }
    }


    fun indexArgumentPosition(format : String) : IntArray
    {
        val list = ArrayList<String>()
        val arrayString = format.split("\\{[0-9]*}".toRegex())
        var target = 0
        for((i,v) in arrayString.withIndex())
        {
            target += v.length
            var sb = v
            var indexSubStr : String? = null
            if(arrayString.size != i + 1)
                indexSubStr = format.substring(target, target + 3)

            if(sb == "" || sb.length < 0x0F)
                sb += randomString()

            list.add(sb)
            if(indexSubStr != null)
                list.add(indexSubStr)
            target += 3
        }
        return indexArgumentPosition0(appendArray(list))
    }

    fun appendArray(format : List<String>) : String
    {
        var str = ""
        for(s in format)
        {
            str += s
        }
        return str
    }

    fun randomString(length : Int = 10) : String
    {
        val temp = StringBuffer()
        val rnd = Random()
        for (i in 0..length)
        {
            val rIndex = rnd.nextInt(3)
            when (rIndex) {
                0 -> temp.append((rnd.nextInt(26) + 97).toChar())
                1 -> temp.append((rnd.nextInt(26) + 65).toChar())
                2 -> temp.append(rnd.nextInt(10))
            }
        }
        return temp.toString()
    }


    private fun indexArgumentPosition0(format : String) : IntArray
    {
        val list = ArrayList<Int>()
        val arrayString = format.split("\\{[0-9]*}".toRegex())
        for(index in 0 until arrayString.size)
        {
            if(index == arrayString.size - 1)
                break

            val ps1 = arrayString[index]
            val ps2 = arrayString[index + 1]
            val p = Pattern.compile("(?i)($ps1)(\\{[0-9]*})(?i)($ps2)")
            val m = p.matcher(format)
            if(m.find())
            {
                var str = m.group(0)
                str = str.replace(ps1, "")
                str = str.replace(ps2, "")
                str = str.substring(1, str.length - 1)
                list.add(Integer.parseInt(str))
            }
        }
        return list.toIntArray()
    }
}