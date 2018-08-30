package io.github.shadowcreative

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import io.github.shadowcreative.shadow.entity.EntityObject
import org.bukkit.Location
import java.lang.reflect.Type


class FinalEntity : TestEntity() {
    private val finalTest1 : Int = 0
    private var finalTest2 : String = "Final"
}

open class TestEntity : EntityObject<TestEntity>()
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TestEntity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val test1 : String = "hello"

    private val test2 : Int = 2

    private val test3 : Float = 0.05f

    private val test4 : Double = 0.0

    private val location : Location = Location(null, 0.0,1.2,2.3,4f,5f)


    init
    {

    }
}

object Main
{
    @JvmStatic
    fun main(args: Array<String>) {
        val test = FinalEntity()
        val result = test.toSerialize()
        print(result.toString())
    }
}
