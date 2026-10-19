package net.infyrium.icommands.commands;

import net.infyrium.icommands.iCommandsMain;

/**
 * /tpaccept [player]: accepts a teleport request.
 */
public class TpacceptCommand extends TpaAnswerCommand {

    public TpacceptCommand(iCommandsMain plugin) {
        super(plugin, "tpaccept", true);
    }
}
