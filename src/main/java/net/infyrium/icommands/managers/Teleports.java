package net.infyrium.icommands.managers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Teleports a player to another player after a delay that moving or taking damage cancels. The teleport fires the
 * usual event, so plugins that guard places (SuperiorSkyblock2 bans and locked islands) can stop it and tell the
 * player why. The delay of a player is 0 with the bypass permission, else the lowest delay permission they have
 * (icommands.tpa.delay.(seconds)), or the default delay without one. Made after Teleports of iHomes.
 */
public class Teleports {

    // How deep the ground may be under the place
    private static final int GROUND_DEPTH = 3;
    private static final Set<Material> DANGER = Set.of(Material.LAVA, Material.LAVA_CAULDRON, Material.FIRE,
            Material.SOUL_FIRE, Material.MAGMA_BLOCK, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.CACTUS);

    private record Pending(BukkitTask task, Location start) {
    }

    private final JavaPlugin plugin;
    private final Messages messages;
    private final String delayPermission;
    private final String bypassPermission;

    private final Map<UUID, Pending> pending = new HashMap<>();

    public Teleports(JavaPlugin plugin, Messages messages, String delayPermission, String bypassPermission) {
        this.plugin = plugin;
        this.messages = messages;
        this.delayPermission = delayPermission;
        this.bypassPermission = bypassPermission;
    }

    /**
     * Teleports the player to where the target stands when the delay ends, defaultDelay seconds without a delay
     * permission. On arrival the player gets the message doneKey. A teleport already waiting for the player is
     * replaced.
     */
    public void start(Player player, Player target, int defaultDelay, String doneKey, TagResolver... placeholders) {
        cancel(player);
        int delay = delayOf(player, defaultDelay);
        if (delay <= 0) {
            teleport(player, target, doneKey, placeholders);
            return;
        }

        messages.send(player, "teleport-delay", Placeholder.unparsed("seconds", String.valueOf(delay)));
        UUID id = player.getUniqueId();
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            pending.remove(id);
            if (player.isOnline()) {
                teleport(player, target, doneKey, placeholders);
            }
        }, delay * 20L);
        pending.put(id, new Pending(task, player.getLocation()));
    }

    /**
     * 0 with the bypass permission, else the lowest delay permission of the player, or defaultDelay without one.
     */
    public int delayOf(Player player, int defaultDelay) {
        if (player.hasPermission(bypassPermission)) return 0;

        int delay = -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission().toLowerCase(Locale.ROOT);
            if (!info.getValue() || !permission.startsWith(delayPermission)) continue;
            try {
                int seconds = Integer.parseInt(permission.substring(delayPermission.length()));
                if (seconds >= 0 && (delay < 0 || seconds < delay)) {
                    delay = seconds;
                }
            } catch (NumberFormatException ignored) {
                // Not a number, not a delay
            }
        }
        return delay < 0 ? defaultDelay : delay;
    }

    /**
     * Cancels the waiting teleport if the player left the block the wait started in.
     */
    public void moved(Player player, Location to) {
        Pending wait = pending.get(player.getUniqueId());
        if (wait == null) return;

        Location start = wait.start();
        if (to.getWorld() == start.getWorld() && to.getBlockX() == start.getBlockX()
                && to.getBlockY() == start.getBlockY() && to.getBlockZ() == start.getBlockZ()) return;

        cancel(player);
        messages.send(player, "teleport-cancelled");
    }

    public void damaged(Player player) {
        if (!pending.containsKey(player.getUniqueId())) return;

        cancel(player);
        messages.send(player, "teleport-cancelled");
    }

    /**
     * Cancels the waiting teleport without a message.
     */
    public void cancel(Player player) {
        Pending wait = pending.remove(player.getUniqueId());
        if (wait != null) {
            wait.task().cancel();
        }
    }

    public void cancelAll() {
        for (Pending wait : pending.values()) {
            wait.task().cancel();
        }
        pending.clear();
    }

    /**
     * Not into lava or fire, not into a wall, and with ground or water close under the feet.
     */
    public static boolean isSafe(Location location) {
        Block feet = location.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        if (DANGER.contains(feet.getType()) || DANGER.contains(head.getType()) || !head.isPassable()) return false;

        if (!feet.isPassable()) {
            // Standing on top of a slab or a carpet is fine, being inside a block is not
            return feet.getBoundingBox().getMaxY() <= location.getY() + 0.01;
        }
        if (feet.getType() == Material.WATER) return true;

        for (int depth = 1; depth <= GROUND_DEPTH; depth++) {
            Block below = feet.getRelative(BlockFace.DOWN, depth);
            if (DANGER.contains(below.getType())) return false;
            if (below.getType() == Material.WATER || !below.isPassable()) return true;
        }
        return false;
    }

    private void teleport(Player player, Player target, String doneKey, TagResolver... placeholders) {
        if (!target.isOnline()) {
            messages.send(player, "teleport-target-offline", Placeholder.unparsed("player", target.getName()));
            return;
        }

        // The world and the chunk of an online player are loaded
        Location location = target.getLocation();
        if (!isSafe(location)) {
            messages.send(player, "teleport-unsafe");
            return;
        }
        // A cancelled teleport is explained by whoever cancelled it
        player.teleportAsync(location, TeleportCause.COMMAND).thenAccept(done -> {
            if (done && player.isOnline()) {
                messages.send(player, doneKey, placeholders);
            }
        });
    }
}
