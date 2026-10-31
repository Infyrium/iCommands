package net.infyrium.icommands.commands;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.infyrium.icommands.iCommandsMain;
import net.infyrium.icommands.managers.Messages;
import net.infyrium.icommands.utils.Completions;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

/**
 * /nick: changes or resets the display name of a player.
 */
public class NickCommand implements TabExecutor {

    private static final Pattern COLOR_CODES = Pattern.compile("&[0-9a-fk-orA-FK-OR]");

    private static final Pattern ALLOWED_NICK = Pattern.compile("^[a-zA-Z_0-9]+$");

    private static final int MAX_NICK_LENGTH = 16;

    private final iCommandsMain plugin;

    public NickCommand(iCommandsMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages = plugin.getMessages();
        Player target;
        String nick;

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                messages.send(sender, "usage-nick-console", Placeholder.unparsed("command", label));
                return true;
            }
            if (plugin.getConfig().getBoolean("settings.requirePermission.nick", true)
                    && !sender.hasPermission("icommands.nick")) {
                messages.send(sender, "no-permission");
                return true;
            }
            target = player;
            nick = args[0];
        } else if (args.length == 2) {
            if (!sender.hasPermission("icommands.nick.others")) {
                messages.send(sender, "no-permission-others");
                return true;
            }
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null || (sender instanceof Player player && !player.canSee(target))) {
                messages.send(sender, "player-not-online", Placeholder.unparsed("player", args[0]));
                return true;
            }
            nick = args[1];
        } else {
            messages.send(sender, "usage-nick", Placeholder.unparsed("command", label));
            return true;
        }

        if (nick.equalsIgnoreCase("off")) {
            plugin.getPlayerData().setNick(target, null);
            messages.send(target, "nick-removed");
        } else {
            if (!sender.hasPermission("icommands.nick.color")) {
                nick = stripColors(nick);
            }

            String error = validate(target, nick);
            if (error != null) {
                messages.send(sender, error);
                return true;
            }

            plugin.getPlayerData().setNick(target, nick);
            messages.send(target, "nick-set", Placeholder.unparsed("nick", stripColors(nick)));
        }

        if (target != sender) {
            messages.send(sender, "nick-changed");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 && sender.hasPermission("icommands.nick.others")
                ? Completions.players(sender, args[0]) : List.of();
    }

    // Returns the message key of the error, or null if the nickname is fine
    private String validate(Player target, String nick) {
        String plain = stripColors(nick);

        if (!ALLOWED_NICK.matcher(plain).matches()) {
            return "nick-invalid";
        }
        if (nick.length() > MAX_NICK_LENGTH) {
            return "nick-too-long";
        }
        if (isNickInUse(target, plain)) {
            return "nick-in-use";
        }
        return null;
    }

    private boolean isNickInUse(Player target, String plain) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(target)) continue;

            String onlineNick = plugin.getPlayerData().getNick(online);
            if (plain.equalsIgnoreCase(online.getName())
                    || (onlineNick != null && plain.equalsIgnoreCase(stripColors(onlineNick)))) {
                return true;
            }
        }

        OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(plain);
        return offline != null && offline.hasPlayedBefore() && !offline.getUniqueId().equals(target.getUniqueId());
    }

    private static String stripColors(String nick) {
        return COLOR_CODES.matcher(nick).replaceAll("");
    }
}
