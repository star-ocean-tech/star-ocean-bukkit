package org.staroceanmc.bukkit.data.impl.yaml;

import org.staroceanmc.bukkit.data.PlayerDataRootStorageFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class YamlPlayerDataRootStorageFactory extends PlayerDataRootStorageFactory<YamlPlayerDataRootStorage> {
    private static final YamlPlayerDataRootStorageFactory INSTANCE = new YamlPlayerDataRootStorageFactory();

    private YamlPlayerDataRootStorageFactory() {}

    @Override
    public YamlPlayerDataRootStorage createRootStorage(Map<String, String> configuration) {

        return null;
    }

    public static YamlPlayerDataRootStorageFactory getInstance() {
        return INSTANCE;
    }
}
