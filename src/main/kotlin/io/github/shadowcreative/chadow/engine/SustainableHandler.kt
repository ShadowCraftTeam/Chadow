package io.github.shadowcreative.chadow.engine

import io.github.shadowcreative.chadow.Handle

abstract class SustainableHandler : Runnable, Handle
{
    private val customData : HashMap<Any, Any?> = HashMap()

    final override fun run()
    {
        this.onInit(customData)
    }
}