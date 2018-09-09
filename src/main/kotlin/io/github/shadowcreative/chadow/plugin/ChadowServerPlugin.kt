package io.github.shadowcreative.chadow.plugin

import io.github.shadowcreative.chadow.Activator
import io.github.shadowcreative.chadow.MessageHandler
import io.github.shadowcreative.chadow.component.Prefix

interface ChadowServerPlugin
{
    fun getMessageHandler(): MessageHandler

    fun getHandleInstance(): Any?

    fun getServerPrefix(): Prefix?

    fun getRegisterHandlers(): List<Activator<IntegratedPlugin>>

    fun reload()
}
