package org.staroceanmc.bukkit.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class PlayerDataRootStorage {

    /**
     * Loads a player's data.
     */
    @NotNull
    public abstract PlayerDataStorage loadForPlayer(@NotNull UUID uuid);

    public abstract boolean requestSave(UUID uuid, PlayerDataStorage storage);

    public abstract boolean saveImmediately(UUID uuid, PlayerDataStorage storage);
}
