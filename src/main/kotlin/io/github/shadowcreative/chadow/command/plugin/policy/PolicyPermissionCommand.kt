package io.github.shadowcreative.chadow.command.plugin.policy

import io.github.shadowcreative.chadow.command.ChadowCommand

class PolicyPermissionCommand : ChadowCommand<PolicyStatusCommand>("permission", "perm")
{
    init {
        this.setPermission("permission")
        this.setCommandDescription("Manage the permission of handled classes")
    }
}
