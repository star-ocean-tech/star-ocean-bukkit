package org.staroceanmc.bukkit.data;

import java.util.Map;

/**
 * Factory to create a root storage.
 * @param <T> correspond storage class.
 */
public abstract class PlayerDataRootStorageFactory<T extends PlayerDataRootStorage> {

    /**
     * Creates a root storage with given configuration.
     */
    public abstract T createRootStorage(Map<String, String> configuration);

}
