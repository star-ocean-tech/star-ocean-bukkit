package org.staroceanmc.bukkit.data;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final Map<UUID, PlayerDataStorage> trackedPlayerData = new HashMap<>();
    private final PlayerDataRootStorage rootStorage;

    public PlayerDataManager(PlayerDataRootStorage rootStorage) {
        this.rootStorage = rootStorage;
    }

    /**
     * NOTE: this method involves blocking IO.
     * Should be invoked in {@link org.bukkit.event.player.AsyncPlayerPreLoginEvent}
     * @param player
     */
    public void loadForPlayer(Player player) {

    }
}
