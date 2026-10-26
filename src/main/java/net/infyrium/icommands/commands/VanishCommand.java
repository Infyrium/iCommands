package net.infyrium.icommands.commands;

import org.bukkit.entity.Player;

import net.infyrium.icommands.iCommandsMain;

public class VanishCommand extends TargetCommand {

    public VanishCommand(iCommandsMain plugin) {
        super(plugin, "vanish");
    }

    @Override
    protected String execute(Player target) {
        boolean enabled = !plugin.getPlayerData().isVanished(target);
        plugin.getPlayerData().setVanished(target, enabled);

        return enabled ? "vanish-enabled" : "vanish-disabled";
    }
}
