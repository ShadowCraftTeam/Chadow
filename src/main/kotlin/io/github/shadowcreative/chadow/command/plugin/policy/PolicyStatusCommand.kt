package io.github.shadowcreative.chadow.command.plugin.policy

import io.github.shadowcreative.chadow.command.ChadowCommand
import io.github.shadowcreative.chadow.command.misc.Parameter

class PolicyStatusCommand : ChadowCommand<PolicyStatusCommand>("status")
{
    init {
        this.setPermission("status")
        this.addParameter(Parameter("pluginname", true))
        this.setCommandDescription("Show status of handled classes or plugin")
    }
}