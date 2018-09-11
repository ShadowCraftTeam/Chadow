package io.github.shadowcreative.chadow.entity

import io.github.shadowcreative.eunit.EntityUnit

class SimpleEntity : EntityUnit<SimpleEntity>()
{
    public val test : String = "Hello world"
    public val test2 : String = "This is test for EntityUnit"
    public val test3 : Array<Double> = arrayOf(10.0, 22.7, -1.0)
}
