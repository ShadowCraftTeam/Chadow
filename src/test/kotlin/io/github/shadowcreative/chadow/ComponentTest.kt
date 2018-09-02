package io.github.shadowcreative.chadow

import io.github.shadowcreative.chadow.component.FormatDescription
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.regex.Pattern

class ComponentTest
{
    fun testComponent() : String
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
        return f.rawMessage()
    }

    @Test
    fun result()
    {
        val testObject = ComponentTest()
        assertEquals("Hello world, player! This is FormatDescription Test", testObject.testComponent())
    }
}
