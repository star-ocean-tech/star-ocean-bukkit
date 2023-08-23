package org.staroceanmc.bukkit.data;

import org.bukkit.persistence.PersistentDataContainer;

public abstract class PlayerDataContainer implements PersistentDataContainer {

    private final PlayerDataAdapters adapters;

    protected PlayerDataContainer(PlayerDataAdapters adapters) {
        this.adapters = adapters;
    }

    public PlayerDataAdapters getAdapters() {
        return adapters;
    }
}
