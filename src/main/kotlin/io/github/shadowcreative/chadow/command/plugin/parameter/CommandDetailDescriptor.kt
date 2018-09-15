package io.github.shadowcreative.chadow.command.plugin.parameter

import io.github.shadowcreative.chadow.command.ChadowCommand
import io.github.shadowcreative.chadow.command.entity.ComponentString
import io.github.shadowcreative.chadow.command.entity.Page
import io.github.shadowcreative.chadow.component.FormatDescription
import io.github.shadowcreative.chadow.util.CommandUtility
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
        reviewCommandDescription.addFilter("command", reviewCommand)


        // Convert to base component.
        var description = CommandUtility.toBaseComponent(reviewCommandDescription) as ComponentString
        componentList.add(description)

        // &aThe framework of command: {current_command} (Included hover message)
        val cmdFramework = FormatDescription("&aThe framework of command: &e/&f")

        description = CommandUtility.toBaseComponent(cmdFramework) as ComponentString
        componentList.add(description)
        componentList.add(CommandUtility.toBaseComponent(this.getCommandDescription()) as ComponentString)
        componentList.add(CommandUtility.toBaseComponent(FormatDescription("Parameters : ")) as ComponentString)
        var parameterDescription : FormatDescription
        for(param in this.getParameters()) {
            parameterDescription = FormatDescription(param.getName() + " | " + param.getDescription()!!.rawMessage())
            componentList.add(CommandUtility.toBaseComponent(parameterDescription) as ComponentString)
        }

        return Page(componentList).execute(sender, ArrayList())
    }
}
