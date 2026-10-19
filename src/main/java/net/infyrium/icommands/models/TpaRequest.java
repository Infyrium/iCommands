package net.infyrium.icommands.models;

import java.util.UUID;

/**
 * A teleport request waiting for an answer until expires. With here the requester calls the target to them,
 * else the requester goes to the target.
 */
public record TpaRequest(UUID requester, String requesterName, boolean here, long expires) {
}
