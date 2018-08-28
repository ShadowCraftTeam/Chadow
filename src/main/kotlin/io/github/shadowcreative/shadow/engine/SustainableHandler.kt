package io.github.shadowcreative.shadow.engine

import io.github.shadowcreative.shadow.Handle

abstract class SustainableHandler : Runnable, Handle
{
    private val customData : HashMap<Any, Any?> = HashMap()

    final override fun run()
    {
        this.onInit(customData)
    }
}