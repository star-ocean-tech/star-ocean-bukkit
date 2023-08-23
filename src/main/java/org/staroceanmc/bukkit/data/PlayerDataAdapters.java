package org.staroceanmc.bukkit.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

// TODO: May be per module basis
public abstract class PlayerDataAdapters implements PersistentDataAdapterContext {

    private final Map<Class<?>, DataAdapter<?, ?>> adapters = new HashMap<>();

    @NotNull
    public abstract PlayerDataContainer newPersistentDataContainer();

    protected static abstract class DataAdapter<P, C> {

        private final PersistentDataType<P, C> dataType;

        protected DataAdapter(PersistentDataType<P, C> dataType) {
            this.dataType = dataType;
        }
    }
}
