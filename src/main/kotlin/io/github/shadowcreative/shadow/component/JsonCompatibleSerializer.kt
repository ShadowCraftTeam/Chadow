package io.github.shadowcreative.shadow.component

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import io.github.shadowcreative.shadow.platform.GenericInstance
import java.util.concurrent.atomic.AtomicMarkableReference

abstract class JsonCompatibleSerializer<A> : JsonDeserializer<A>, JsonSerializer<A>
{
    constructor(reference: Class<A>)
    {
        this.reference = reference
    }

    private val reference : Class<A>
    fun getReference() : Class<A> = this.reference
}