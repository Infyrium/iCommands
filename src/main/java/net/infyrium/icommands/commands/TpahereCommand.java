package net.infyrium.icommands.commands;

import net.infyrium.icommands.iCommandsMain;

/**
 * /tpahere (player): asks the player to teleport to the sender.
 */
public class TpahereCommand extends TpaRequestCommand {

    public TpahereCommand(iCommandsMain plugin) {
        super(plugin, "tpahere", true);
    }
}
