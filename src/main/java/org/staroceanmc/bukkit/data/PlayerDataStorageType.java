package org.staroceanmc.bukkit.data;

import org.staroceanmc.bukkit.data.impl.yaml.YamlPlayerDataRootStorage;
import org.staroceanmc.bukkit.data.impl.yaml.YamlPlayerDataRootStorageFactory;

import java.util.Map;
import java.util.function.Function;

public enum PlayerDataStorageType {
    YAML(YamlPlayerDataRootStorage.class, YamlPlayerDataRootStorageFactory.getInstance()::createRootStorage);

    private final Class<? extends PlayerDataRootStorage> implClass;
    private final Function<Map<String, String>, PlayerDataRootStorage> constructor;

    PlayerDataStorageType(Class<? extends PlayerDataRootStorage> implClass, Function<Map<String, String>, PlayerDataRootStorage> constructor) {
        this.implClass = implClass;
        this.constructor = constructor;
    }

    public PlayerDataRootStorage create(Map<String, String> config) {
        return constructor.apply(config);
    }
}
