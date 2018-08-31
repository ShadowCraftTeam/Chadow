package io.github.shadowcreative.chadow.component

import java.util.regex.Pattern

object Test
{
    @JvmStatic
    fun main(args : Array<String>)
    {
        val f = FormatDescription("Hello world, {name}! This is {test_code}")
        f.addFilter("name", "player")
        f.addFilter("test_code", "FormatDescription Test")
        for(k in f.getFilter().keys)
        {
            val pattern = Pattern.compile("(\\{\\b$k})")
            val match = pattern.matcher(f.getFormat())
            match.find()
            if(match.groupCount() != 0)
            {
                val value : String = f.getFilter()[k]!!
                f.setFormat(f.getFormat().replace("\\{\\b$k}".toRegex(), value))
            }
        }
        println(f.rawMessage())
    }

}
