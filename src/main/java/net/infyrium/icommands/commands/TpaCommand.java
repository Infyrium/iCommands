package net.infyrium.icommands.commands;

import net.infyrium.icommands.iCommandsMain;

/**
 * /tpa (player): asks to teleport to the player.
 */
public class TpaCommand extends TpaRequestCommand {

    public TpaCommand(iCommandsMain plugin) {
        super(plugin, "tpa", false);
    }
}
