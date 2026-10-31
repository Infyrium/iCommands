package net.infyrium.icommands;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.infyrium.icommands.commands.ExtCommand;
import net.infyrium.icommands.commands.FeedCommand;
import net.infyrium.icommands.commands.FlyCommand;
import net.infyrium.icommands.commands.GodCommand;
import net.infyrium.icommands.commands.HealCommand;
import net.infyrium.icommands.commands.NickCommand;
import net.infyrium.icommands.commands.SpeedCommand;
import net.infyrium.icommands.commands.TpaCommand;
import net.infyrium.icommands.commands.TpacancelCommand;
import net.infyrium.icommands.commands.TpacceptCommand;
import net.infyrium.icommands.commands.TpahereCommand;
import net.infyrium.icommands.commands.TpdenyCommand;
import net.infyrium.icommands.commands.TptoggleCommand;
import net.infyrium.icommands.commands.VanishCommand;
import net.infyrium.icommands.listeners.GodModeListener;
import net.infyrium.icommands.listeners.NickListener;
import net.infyrium.icommands.listeners.TeleportListener;
import net.infyrium.icommands.listeners.VanishListener;
import net.infyrium.icommands.managers.Messages;
import net.infyrium.icommands.managers.PlayerData;
import net.infyrium.icommands.managers.Teleports;
import net.infyrium.icommands.managers.TpaRequests;
import net.infyrium.icommands.utils.ConfigUpdater;

public class iCommandsMain extends JavaPlugin {

    private final TpaRequests tpaRequests = new TpaRequests();

    private Messages messages;

    private Teleports teleports;

    private PlayerData playerData;

    @Override
    public void onEnable() {
        ConfigUpdater.backupIfOutdated(this, "config.yml");
        saveDefaultConfig();

        messages = new Messages(this);
        messages.load();

        playerData = new PlayerData(this);

        teleports = new Teleports(this, messages, "icommands.tpa.delay.", "icommands.tpa.bypass.delay");

        getServer().getPluginManager().registerEvents(new GodModeListener(this), this);
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
        getServer().getPluginManager().registerEvents(new NickListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportListener(teleports, tpaRequests), this);

        Map<String, TabExecutor> commands = Map.ofEntries(
                Map.entry("feed", new FeedCommand(this)),
                Map.entry("heal", new HealCommand(this)),
                Map.entry("fly", new FlyCommand(this)),
                Map.entry("speed", new SpeedCommand(this)),
                Map.entry("god", new GodCommand(this)),
                Map.entry("ext", new ExtCommand(this)),
                Map.entry("vanish", new VanishCommand(this)),
                Map.entry("nick", new NickCommand(this)),
                Map.entry("tpa", new TpaCommand(this)),
                Map.entry("tpahere", new TpahereCommand(this)),
                Map.entry("tpaccept", new TpacceptCommand(this)),
                Map.entry("tpdeny", new TpdenyCommand(this)),
                Map.entry("tpacancel", new TpacancelCommand(this)),
                Map.entry("tptoggle", new TptoggleCommand(this)));
        for (Map.Entry<String, TabExecutor> entry : commands.entrySet()) {
            PluginCommand command = getCommand(entry.getKey());
            if (command != null) {
                command.setExecutor(entry.getValue());
                command.setTabCompleter(entry.getValue());
            }
        }

        // Players are already online after a plugin reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerData.isVanished(player)) {
                playerData.applyVanish(player);
            }
        }

        getLogger().info("Plugin has been enabled!");
        getLogger().info("Plugin developed by: " + String.join(", ", getPluginMeta().getAuthors()));
        getLogger().info("Website: " + getPluginMeta().getWebsite());
    }

    @Override
    public void onDisable() {
        if (teleports != null) {
            teleports.cancelAll();
        }
        tpaRequests.clear();

        // Vanish stays saved, but players can't stay hidden by a disabled plugin
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerData.isVanished(player)) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.showPlayer(this, player);
                }
            }
        }

        getLogger().info("Plugin has been disabled!");
    }

    /**
     * Whether the command needs its permission, settings.requirePermission in config.yml.
     */
    public boolean requiresPermission(String command) {
        return getConfig().getBoolean("settings.requirePermission." + command, true);
    }

    /**
     * Checks the permission of the command and tells the sender if it is missing.
     */
    public boolean hasCommandPermission(CommandSender sender, String command) {
        if (!requiresPermission(command) || sender.hasPermission("icommands." + command)) return true;
        messages.send(sender, "no-permission");
        return false;
    }

    public TpaRequests getTpaRequests() {
        return tpaRequests;
    }

    public Messages getMessages() {
        return messages;
    }

    public Teleports getTeleports() {
        return teleports;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }
}
