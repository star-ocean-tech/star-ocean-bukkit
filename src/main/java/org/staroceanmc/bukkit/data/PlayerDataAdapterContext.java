package org.staroceanmc.bukkit.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class PlayerDataAdapterContext implements PersistentDataAdapterContext {

    @Override
    public @NotNull PersistentDataContainer newPersistentDataContainer() {
        return null;
    }
}
