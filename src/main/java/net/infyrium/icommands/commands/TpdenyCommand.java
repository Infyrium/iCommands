package net.infyrium.icommands.commands;

import net.infyrium.icommands.iCommandsMain;

/**
 * /tpdeny [player]: denies a teleport request.
 */
public class TpdenyCommand extends TpaAnswerCommand {

    public TpdenyCommand(iCommandsMain plugin) {
        super(plugin, "tpdeny", false);
    }
}
