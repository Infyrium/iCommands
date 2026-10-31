package net.infyrium.icommands.commands;

import org.bukkit.entity.Player;

import net.infyrium.icommands.iCommandsMain;

/**
 * /ext: extinguishes a player.
 */
public class ExtCommand extends TargetCommand {

    public ExtCommand(iCommandsMain plugin) {
        super(plugin, "ext");
    }

    @Override
    protected String execute(Player target) {
        target.setFireTicks(0);
        return "ext";
    }
}
