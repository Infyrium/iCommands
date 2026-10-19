package net.infyrium.icommands.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.infyrium.icommands.managers.Teleports;
import net.infyrium.icommands.managers.TpaRequests;

/**
 * Moving or taking damage cancels a teleport that waits for its delay. A player who leaves takes their teleport
 * requests with them.
 */
public class TeleportListener implements Listener {

    private final Teleports teleports;
    private final TpaRequests requests;

    public TeleportListener(Teleports teleports, TpaRequests requests) {
        this.teleports = teleports;
        this.requests = requests;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        teleports.moved(event.getPlayer(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            teleports.damaged(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        teleports.cancel(event.getPlayer());
        requests.remove(event.getPlayer().getUniqueId());
    }
}
