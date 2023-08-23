package org.staroceanmc.bukkit.data;

import org.staroceanmc.bukkit.Module;

import java.util.UUID;

public interface PlayerDataStorage {

    PlayerDataContainer loadModuleData(Module module);

    boolean requestSave(Module module);

    boolean saveImmediately(Module module);

    UUID getPlayerUniqueId();
}
