package net.treset.adaptiveview.tools;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public enum NotificationState {
    NONE,
    ADDED,
    REMOVED;

    public static NotificationState getFromPlayer(ServerPlayerEntity player, List<String> broadcastTo) {
        for(String s : broadcastTo) {
            if(player.getName().getString().equalsIgnoreCase(s)) {
                return ADDED;
            }
            if(s.startsWith("!") && player.getName().getString().equalsIgnoreCase(s.substring(1))) {
                return REMOVED;
            }
        }
        return NONE;
    }
}
