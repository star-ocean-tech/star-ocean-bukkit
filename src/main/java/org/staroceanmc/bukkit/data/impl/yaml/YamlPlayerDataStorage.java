package org.staroceanmc.bukkit.data.impl.yaml;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.staroceanmc.bukkit.Module;
import org.staroceanmc.bukkit.data.PlayerDataContainer;
import org.staroceanmc.bukkit.data.PlayerDataStorage;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class YamlPlayerDataStorage implements PlayerDataStorage {
    private final FileConfiguration data = new YamlConfiguration();

    @Override
    public PlayerDataContainer loadModuleData(Module module) {
        String moduleName = module.getModuleName();

        return null;
    }

    @Override
    public boolean requestSave(Module module) {
        return false;
    }

    @Override
    public boolean saveImmediately(Module module) {
        return false;
    }

    @Override
    public UUID getPlayerUniqueId() {
        return null;
    }
}
