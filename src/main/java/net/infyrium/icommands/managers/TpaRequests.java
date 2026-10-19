package net.infyrium.icommands.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.infyrium.icommands.models.TpaRequest;

/**
 * Teleport requests waiting for an answer, in memory and on the server thread only. A player keeps up to MAX
 * requests, a new one pushes out the oldest. A new request from the same player replaces the old one.
 */
public class TpaRequests {

    public static final long SECONDS = 60L;
    public static final int MAX = 5;

    // target -> requests by requester, the oldest first
    private final Map<UUID, LinkedHashMap<UUID, TpaRequest>> requests = new HashMap<>();

    /**
     * Adds the request. False if the same request from this requester is still waiting.
     */
    public boolean add(UUID target, UUID requester, String requesterName, boolean here) {
        LinkedHashMap<UUID, TpaRequest> list = requests.computeIfAbsent(target, key -> new LinkedHashMap<>());
        TpaRequest old = list.remove(requester);
        if (old != null && old.here() == here && old.expires() >= System.currentTimeMillis()) {
            list.put(requester, old);
            return false;
        }

        removeExpired(list);
        while (list.size() >= MAX) {
            list.pollFirstEntry();
        }
        list.put(requester, new TpaRequest(requester, requesterName, here, System.currentTimeMillis() + SECONDS * 1000L));
        return true;
    }

    /**
     * Takes the newest request to the target, from the requester when it is not null. Null if there is none.
     */
    public TpaRequest take(UUID target, UUID requester) {
        LinkedHashMap<UUID, TpaRequest> list = requests.get(target);
        if (list == null) return null;

        removeExpired(list);
        TpaRequest found = null;
        if (requester != null) {
            found = list.remove(requester);
        } else if (!list.isEmpty()) {
            found = list.pollLastEntry().getValue();
        }
        if (list.isEmpty()) {
            requests.remove(target);
        }
        return found;
    }

    /**
     * Removes the waiting requests of the requester, only the one to the target when it is not null.
     * Returns the targets.
     */
    public List<UUID> cancel(UUID requester, UUID target) {
        List<UUID> targets = new ArrayList<>();
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, LinkedHashMap<UUID, TpaRequest>>> entries = requests.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<UUID, LinkedHashMap<UUID, TpaRequest>> entry = entries.next();
            if (target != null && !entry.getKey().equals(target)) continue;

            TpaRequest request = entry.getValue().remove(requester);
            if (request != null && request.expires() >= now) {
                targets.add(entry.getKey());
            }
            if (entry.getValue().isEmpty()) {
                entries.remove();
            }
        }
        return targets;
    }

    /**
     * The names of the players whose requests the target can still answer, for tab completion.
     */
    public List<String> getRequesters(UUID target) {
        LinkedHashMap<UUID, TpaRequest> list = requests.get(target);
        if (list == null) return List.of();

        long now = System.currentTimeMillis();
        List<String> names = new ArrayList<>();
        for (TpaRequest request : list.values()) {
            if (request.expires() >= now) {
                names.add(request.requesterName());
            }
        }
        return names;
    }

    /**
     * The players the requester still waits for, for tab completion.
     */
    public List<UUID> getTargets(UUID requester) {
        long now = System.currentTimeMillis();
        List<UUID> targets = new ArrayList<>();
        for (Map.Entry<UUID, LinkedHashMap<UUID, TpaRequest>> entry : requests.entrySet()) {
            TpaRequest request = entry.getValue().get(requester);
            if (request != null && request.expires() >= now) {
                targets.add(entry.getKey());
            }
        }
        return targets;
    }

    /**
     * Forgets the requests to and from the player who left.
     */
    public void remove(UUID player) {
        requests.remove(player);
        cancel(player, null);
    }

    public void clear() {
        requests.clear();
    }

    private static void removeExpired(LinkedHashMap<UUID, TpaRequest> list) {
        long now = System.currentTimeMillis();
        list.values().removeIf(request -> request.expires() < now);
    }
}
