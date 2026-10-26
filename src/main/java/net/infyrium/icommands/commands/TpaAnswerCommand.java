package net.infyrium.icommands.commands;

import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.infyrium.icommands.iCommandsMain;
import net.infyrium.icommands.managers.Messages;
import net.infyrium.icommands.models.TpaRequest;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

/**
 * Base for /tpaccept and /tpdeny [player]: answers the newest teleport request, or the one from the player.
 */
public abstract class TpaAnswerCommand implements TabExecutor {

    protected final iCommandsMain plugin;

    private final String name;

    private final boolean accept;

    protected TpaAnswerCommand(iCommandsMain plugin, String name, boolean accept) {
        this.plugin = plugin;
        this.name = name;
        this.accept = accept;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages = plugin.getMessages();
        if (!(sender instanceof Player player)) {
            messages.send(sender, "player-only");
            return true;
        }
        if (!plugin.hasCommandPermission(player, name)) return true;

        Player from = null;
        if (args.length > 0) {
            from = Bukkit.getPlayerExact(args[0]);
            if (from == null || !player.canSee(from)) {
                messages.send(player, "tpa-none-from", Placeholder.unparsed("player", args[0]));
                return true;
            }
        }

        TpaRequest request = plugin.getTpaRequests().take(player.getUniqueId(), from == null ? null : from.getUniqueId());
        Player requester = request == null ? null : Bukkit.getPlayer(request.requester());
        // The requester must still be allowed to ask, as in Essentials
        if (requester != null && accept) {
            String asked = request.here() ? "tpahere" : "tpa";
            if (plugin.requiresPermission(asked) && !requester.hasPermission("icommands." + asked)) {
                requester = null;
            }
        }
        if (requester == null) {
            if (from == null) {
                messages.send(player, "tpa-none");
            } else {
                messages.send(player, "tpa-none-from", Placeholder.unparsed("player", from.getName()));
            }
            return true;
        }

        if (!accept) {
            messages.send(player, "tpa-denied", Placeholder.unparsed("player", requester.getName()));
            messages.send(requester, "tpa-denied-requester", Placeholder.unparsed("player", player.getName()));
            return true;
        }

        messages.send(player, "tpa-accepted", Placeholder.unparsed("player", requester.getName()));
        messages.send(requester, "tpa-accepted-requester", Placeholder.unparsed("player", player.getName()));

        // The one who moves waits for their own delay
        Player moving = request.here() ? player : requester;
        Player target = request.here() ? requester : player;
        plugin.getTeleports().start(moving, target, plugin.getConfig().getInt("settings.teleportDelay", 3),
                "tpa-teleported", Placeholder.unparsed("player", target.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length != 1) return List.of();

        String start = args[0].toLowerCase(Locale.ROOT);
        return plugin.getTpaRequests().getRequesters(player.getUniqueId()).stream()
                .filter(requester -> requester.toLowerCase(Locale.ROOT).startsWith(start))
                .toList();
    }
}
