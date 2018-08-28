package io.github.shadowcreative.shadow.command.plugin.policy

import io.github.shadowcreative.shadow.command.RuskitCommand

class PolicyPermissionCommand : RuskitCommand<PolicyStatusCommand>("permission", "perm")
{
    init {
        this.setPermission("permission")
        this.setCommandDescription("Manage the permission of handled classes")
    }
}
