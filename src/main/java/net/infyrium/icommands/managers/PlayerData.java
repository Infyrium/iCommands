package net.infyrium.icommands.managers;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import net.infyrium.icommands.utils.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * God mode, vanish, nickname and /tptoggle of players, stored in their data, so they stay after rejoin.
 */
public class PlayerData {

    private final JavaPlugin plugin;

    // Online vanished players, safe to read from the async server list ping
    private final Set<UUID> vanishedOnline = ConcurrentHashMap.newKeySet();

    private final NamespacedKey godModeKey;

    private final NamespacedKey vanishKey;

    private final NamespacedKey nickKey;

    private final NamespacedKey tpToggleKey;

    public PlayerData(JavaPlugin plugin) {
        this.plugin = plugin;
        this.godModeKey = new NamespacedKey(plugin, "god");
        this.vanishKey = new NamespacedKey(plugin, "vanish");
        this.nickKey = new NamespacedKey(plugin, "nick");
        this.tpToggleKey = new NamespacedKey(plugin, "tptoggle");
    }

    /**
     * God mode is stored in the player's data, so it stays after rejoin.
     */
    public boolean isGodMode(Player player) {
        return player.getPersistentDataContainer().has(godModeKey);
    }

    public void setGodMode(Player player, boolean enabled) {
        if (enabled) {
            player.getPersistentDataContainer().set(godModeKey, PersistentDataType.BOOLEAN, true);
        } else {
            player.getPersistentDataContainer().remove(godModeKey);
            // Older versions used the vanilla invulnerable flag
            player.setInvulnerable(false);
        }
    }

    /**
     * Vanish is stored in the player's data, so it stays after rejoin.
     */
    public boolean isVanished(Player player) {
        return player.getPersistentDataContainer().has(vanishKey);
    }

    public void setVanished(Player player, boolean enabled) {
        if (enabled) {
            player.getPersistentDataContainer().set(vanishKey, PersistentDataType.BOOLEAN, true);
        } else {
            player.getPersistentDataContainer().remove(vanishKey);
        }
        applyVanish(player);
    }

    /**
     * Hides the player from everyone without icommands.vanish.see, or shows them again.
     */
    public void applyVanish(Player player) {
        boolean enabled = isVanished(player);

        if (enabled) {
            vanishedOnline.add(player.getUniqueId());
        } else {
            vanishedOnline.remove(player.getUniqueId());
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player)) continue;

            if (enabled && !online.hasPermission("icommands.vanish.see")) {
                online.hidePlayer(plugin, player);
            } else {
                online.showPlayer(plugin, player);
            }
        }

        player.setSleepingIgnored(enabled);
        player.setCollidable(!enabled);
        player.setAffectsSpawning(!enabled);
    }

    public Set<UUID> getVanishedOnline() {
        return vanishedOnline;
    }

    /**
     * Returns the nickname with color codes, or null if the player has none.
     */
    public String getNick(Player player) {
        return player.getPersistentDataContainer().get(nickKey, PersistentDataType.STRING);
    }

    /**
     * Nickname is stored in the player's data, so it stays after rejoin. Null removes it.
     */
    public void setNick(Player player, String nick) {
        if (nick == null) {
            player.getPersistentDataContainer().remove(nickKey);
        } else {
            player.getPersistentDataContainer().set(nickKey, PersistentDataType.STRING, nick);
        }
        applyNick(player);
    }

    /**
     * Shows the nickname in chat and in the player list.
     */
    public void applyNick(Player player) {
        String nick = getNick(player);
        // Only letters, digits and color codes, checked by /nick
        Component name = nick == null ? null : MiniMessage.miniMessage().deserialize(Colors.toMiniMessage(nick));
        player.displayName(name);
        player.playerListName(name);
    }

    /**
     * Teleport requests turned off with /tptoggle, stored in the player's data, so it stays after rejoin.
     */
    public boolean isTpToggled(Player player) {
        return player.getPersistentDataContainer().has(tpToggleKey);
    }

    public void setTpToggled(Player player, boolean toggled) {
        if (toggled) {
            player.getPersistentDataContainer().set(tpToggleKey, PersistentDataType.BOOLEAN, true);
        } else {
            player.getPersistentDataContainer().remove(tpToggleKey);
        }
    }
}
