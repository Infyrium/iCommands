package net.infyrium.icommands.utils;

import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Tab completion shared by the commands.
 */
public final class Completions {

    private Completions() {
    }

    /**
     * Names of the players on this server the sender can see, starting with the typed text.
     */
    public static List<String> players(CommandSender sender, String typed) {
        String prefix = typed.toLowerCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> !(sender instanceof Player viewer) || viewer.canSee(player))
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                .toList();
    }

    /**
     * The options starting with the typed text, ignoring case.
     */
    public static List<String> matching(String typed, List<String> options) {
        String prefix = typed.toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
    }
}
