package net.infyrium.icommands.commands;

import org.bukkit.entity.Player;

import net.infyrium.icommands.iCommandsMain;

/**
 * /tptoggle: toggles teleport requests.
 */
public class TptoggleCommand extends TargetCommand {

    public TptoggleCommand(iCommandsMain plugin) {
        super(plugin, "tptoggle");
    }

    @Override
    protected String execute(Player target) {
        boolean toggled = !plugin.getPlayerData().isTpToggled(target);
        plugin.getPlayerData().setTpToggled(target, toggled);

        return toggled ? "tptoggle-disabled" : "tptoggle-enabled";
    }
}
