package io.github.shadowcreative.chadow.command.plugin

import io.github.shadowcreative.chadow.command.ChadowCommand
import io.github.shadowcreative.chadow.command.misc.Parameter

open class PluginCommand : ChadowCommand<PluginCommand>("plugin", "pl")
{
    init {
        this.setCommandDescription("Show all plugins that depend on ChadowIntegratedPlugin")
        this.setPermission("plugin")
        this.setDefaultUser(false)
        this.addParameter(Parameter("plugin_name", true), Parameter("options"))
    }
}
