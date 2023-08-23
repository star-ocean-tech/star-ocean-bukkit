package org.staroceanmc.bukkit.data.impl.yaml;

import org.jetbrains.annotations.NotNull;
import org.staroceanmc.bukkit.data.PlayerDataContainer;
import org.staroceanmc.bukkit.data.PlayerDataRootStorage;
import org.staroceanmc.bukkit.data.PlayerDataStorage;

import java.util.UUID;

public class YamlPlayerDataRootStorage extends PlayerDataRootStorage {
    private final String rootPath;

    public YamlPlayerDataRootStorage(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public @NotNull PlayerDataStorage loadForPlayer(@NotNull UUID uuid) {
        return null;
    }

    @Override
    public boolean requestSave(UUID uuid, PlayerDataStorage storage) {
        return false;
    }

    @Override
    public boolean saveImmediately(UUID uuid, PlayerDataStorage storage) {
        return false;
    }


}
