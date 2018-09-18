package io.github.shadowcreative.chadow.command.plugin.parameter

import io.github.shadowcreative.chadow.command.ChadowCommand
import io.github.shadowcreative.chadow.command.entity.ComponentString
import io.github.shadowcreative.chadow.command.entity.Page
import io.github.shadowcreative.chadow.component.FormatDescription
import org.bukkit.command.CommandSender

class CommandDetailDescriptor : ChadowCommand<CommandDetailDescriptor>("help", "page", "?")
{
    init {
        val description = FormatDescription("Shows the detail command")
        this.setCommandDescription(description)
        this.setPermission("detail")
        this.setDefaultOP(true)
        this.setDefaultUser(true)
    }

    @Suppress("UNCHECKED_CAST")
    override fun perform(sender: CommandSender, argc: Int, argv: List<String>?, handleInstance: Any?): Any?
    {
        // Following this format string:
        //
        // Review of command - main.child.child2.child3 ...
        // Need Permisssion: [main.child.child2] (If the player has permission, it changes green color)
        // This is main command description.
        //
        // Parameter:
        // [child] : This is child description, It shows detail string if child has description. Optional/Required.
        // [child2] : This is child2 description, It shows detail string if child2 has description. Optional/Required.

        // Create list which shows the string commands.
        val componentList = ArrayList<ComponentString>()

        val reviewCommand = this.getCurrentCommand(sender).rawMessage()

        val reviewCommandDescription = FormatDescription("&eReview of command - &b{command}")
        reviewCommandDescription.addFilter("command", reviewCommand.replace(" ", ""))

        var needPermissionDescription : FormatDescription? = null
        val permission = this.getRelativePermission()
        if(permission != null) {
            val hasMessage : String = if(sender.hasPermission(permission.getPermissionName())) {
                "&You have already this permission!"
            } else {
                "&You haven't this permission."
            }
            needPermissionDescription = FormatDescription("{0}: [{1}]")
            needPermissionDescription.setDescriptionSelector(0, "&fRequired permission: ")
            needPermissionDescription.setDescriptionSelector(1, permission.getPermissionName(), hasMessage)
        }

        // Input a part following it:
        // Review of command - main.child.child2.child3 ...
        // Required permission: [main.child.child2]
        // This is main command description.
        //
        componentList.add(reviewCommandDescription.getBaseComponent())
        componentList.add(needPermissionDescription!!.getBaseComponent())
        componentList.add(this.getCommandDescription().getBaseComponent())
        componentList.add(FormatDescription(" ").getBaseComponent())

        componentList.add(FormatDescription("Parameters:").getBaseComponent())

        var parameterDescription : FormatDescription
        for(parameter in this.getParameters()) {
            parameterDescription = FormatDescription("[ {0} ] : {1}")
            parameterDescription.setDescriptionSelector(0, parameter.getName())

            val d = parameter.getDescription()
            var description: String
            description = d?.rawMessage() ?: "No description of the parameter"
            parameterDescription.setDescriptionSelector(1, description)
            componentList.add(parameterDescription.getBaseComponent())
        }

        return Page(componentList).execute(sender, ArrayList())
    }
}
