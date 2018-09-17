package io.github.shadowcreative.chadow.entity

import io.github.shadowcreative.eunit.EntityUnit

class SimpleEntity : EntityUnit<SimpleEntity>
{
    constructor() : super()

    // The constructor is designed to accept a single String value.
    // This will be required if automatic serialization is required.
    constructor(s : String) : super(s)

    public val test : String = "Hello world"
    public val test2 : String = "This is test for EntityUnit"
    public val test3 : Array<Double> = arrayOf(10.0, 22.7, -1.0)
}
