package net.infyrium.icommands.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.infyrium.icommands.iCommandsMain;
import net.infyrium.icommands.managers.Messages;
import net.infyrium.icommands.managers.TpaRequests;
import net.infyrium.icommands.utils.Completions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

/**
 * Base for /tpa and /tpahere: sends a teleport request the target answers with /tpaccept or /tpdeny.
 */
public abstract class TpaRequestCommand implements TabExecutor {

    protected final iCommandsMain plugin;

    private final String name;

    private final boolean here;

    protected TpaRequestCommand(iCommandsMain plugin, String name, boolean here) {
        this.plugin = plugin;
        this.name = name;
        this.here = here;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages = plugin.getMessages();
        if (!(sender instanceof Player player)) {
            messages.send(sender, "player-only");
            return true;
        }
        if (!plugin.hasCommandPermission(player, name)) return true;
        if (args.length != 1) {
            messages.send(player, "usage-player", Placeholder.unparsed("command", label));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        // Players hidden from the sender (e.g. vanished) look offline
        if (target == null || !player.canSee(target)) {
            messages.send(player, "player-not-online", Placeholder.unparsed("player", args[0]));
            return true;
        }
        if (target.equals(player)) {
            messages.send(player, "tpa-self");
            return true;
        }

        if (plugin.getPlayerData().isTpToggled(target)) {
            messages.send(player, "tpa-disabled", Placeholder.unparsed("player", target.getName()));
            return true;
        }
        if (plugin.requiresPermission("tpaccept") && !target.hasPermission("icommands.tpaccept")) {
            messages.send(player, "tpa-no-accept", Placeholder.unparsed("player", target.getName()));
            return true;
        }
        if (!plugin.getTpaRequests().add(target.getUniqueId(), player.getUniqueId(), player.getName(), here)) {
            messages.send(player, "tpa-already", Placeholder.unparsed("player", target.getName()));
            return true;
        }

        String seconds = String.valueOf(TpaRequests.SECONDS);
        messages.send(player, here ? "tpahere-sent" : "tpa-sent", Placeholder.unparsed("player", target.getName()),
                Placeholder.unparsed("seconds", seconds));

        Component accept = messages.get("tpa-accept-button")
                .clickEvent(ClickEvent.runCommand("/tpaccept " + player.getName()));
        Component deny = messages.get("tpa-deny-button")
                .clickEvent(ClickEvent.runCommand("/tpdeny " + player.getName()));
        messages.send(target, here ? "tpahere-received" : "tpa-received",
                Placeholder.unparsed("player", player.getName()), Placeholder.unparsed("seconds", seconds),
                Placeholder.component("accept", accept), Placeholder.component("deny", deny));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? Completions.players(sender, args[0]) : List.of();
    }
}
