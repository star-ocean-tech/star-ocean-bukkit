package org.staroceanmc.bukkit.data.impl.yaml;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.staroceanmc.bukkit.data.PlayerDataAdapters;
import org.staroceanmc.bukkit.data.PlayerDataContainer;

import java.io.IOException;
import java.util.Set;

public class YamlPlayerDataContainer extends PlayerDataContainer {

    protected YamlPlayerDataContainer(PlayerDataAdapters adapters) {
        super(adapters);
    }

    @Override
    public <T, Z> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {

    }

    @Override
    public <T, Z> boolean has(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return false;
    }

    @Override
    public <T, Z> @Nullable Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return null;
    }

    @Override
    public <T, Z> @NotNull Z getOrDefault(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z defaultValue) {
        return null;
    }

    @Override
    public @NotNull Set<NamespacedKey> getKeys() {
        return null;
    }

    @Override
    public void remove(@NotNull NamespacedKey key) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NotNull PersistentDataAdapterContext getAdapterContext() {
        return null;
    }

    @Override
    public boolean has(@NotNull NamespacedKey key) {
        return false;
    }

    @Override
    public byte @NotNull [] serializeToBytes() throws IOException {
        return new byte[0];
    }

    @Override
    public void readFromBytes(byte @NotNull [] bytes, boolean clear) throws IOException {

    }
}
