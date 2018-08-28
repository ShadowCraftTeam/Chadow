package io.github.shadowcreative.shadow.command.plugin.policy

import io.github.shadowcreative.shadow.command.RuskitCommand
import io.github.shadowcreative.shadow.command.misc.Parameter

class PolicyStatusCommand : RuskitCommand<PolicyStatusCommand>("status")
{
    init {
        this.setPermission("status")
        this.addParameter(Parameter("pluginname", true))
        this.setCommandDescription("Show status of handled classes or plugin")
    }
}