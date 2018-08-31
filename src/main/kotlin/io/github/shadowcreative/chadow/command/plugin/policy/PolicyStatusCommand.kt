package io.github.shadowcreative.chadow.command.plugin.policy

import io.github.shadowcreative.chadow.command.RuskitCommand
import io.github.shadowcreative.chadow.command.misc.Parameter

class PolicyStatusCommand : RuskitCommand<PolicyStatusCommand>("status")
{
    init {
        this.setPermission("status")
        this.addParameter(Parameter("pluginname", true))
        this.setCommandDescription("Show status of handled classes or plugin")
    }
}