package io.github.shadowcreative

import io.github.shadowcreative.chadow.entity.Entity
import org.bukkit.Location


class FinalEntity : TestEntity() {
    private val finalTest1 : Int = 0
    private var finalTest2 : String = "Final"
}

open class TestEntity : Entity<TestEntity>()
{
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
        val test = FinalEntity().create()
        val result = test.toSerialize()
        print(result.toString())
    }
}
