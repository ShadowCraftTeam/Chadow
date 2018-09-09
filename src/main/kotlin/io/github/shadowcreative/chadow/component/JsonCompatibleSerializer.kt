package io.github.shadowcreative.chadow.component

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer

abstract class JsonCompatibleSerializer<A> : JsonDeserializer<A>, JsonSerializer<A>
{
    constructor(reference: Class<A>)
    {
        this.reference = reference
    }

    private val reference : Class<A>
    fun getReference() : Class<A> = this.reference
}