package net.infyrium.icommands.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.infyrium.icommands.iCommandsMain;
import net.infyrium.icommands.managers.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

/**
 * /tpacancel [player]: takes back the sent teleport requests, or only the one to the player.
 */
public class TpacancelCommand implements TabExecutor {

    private final iCommandsMain plugin;

    public TpacancelCommand(iCommandsMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages = plugin.getMessages();
        if (!(sender instanceof Player player)) {
            messages.send(sender, "player-only");
            return true;
        }
        if (!plugin.hasCommandPermission(player, "tpacancel")) return true;

        UUID only = null;
        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !player.canSee(target)) {
                messages.send(player, "player-not-online", Placeholder.unparsed("player", args[0]));
                return true;
            }
            only = target.getUniqueId();
        }

        List<UUID> targets = plugin.getTpaRequests().cancel(player.getUniqueId(), only);
        if (targets.isEmpty()) {
            messages.send(player, "tpa-cancel-none");
            return true;
        }
        for (UUID id : targets) {
            Player target = Bukkit.getPlayer(id);
            if (target == null) continue;

            messages.send(player, "tpa-cancelled", Placeholder.unparsed("player", target.getName()));
            messages.send(target, "tpa-cancelled-target", Placeholder.unparsed("player", player.getName()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length != 1) return List.of();

        String start = args[0].toLowerCase(Locale.ROOT);
        List<String> names = new ArrayList<>();
        for (UUID id : plugin.getTpaRequests().getTargets(player.getUniqueId())) {
            Player target = Bukkit.getPlayer(id);
            if (target != null && player.canSee(target) && target.getName().toLowerCase(Locale.ROOT).startsWith(start)) {
                names.add(target.getName());
            }
        }
        return names;
    }
}
