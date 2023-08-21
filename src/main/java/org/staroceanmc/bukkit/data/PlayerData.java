package org.staroceanmc.bukkit.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class PlayerData {

    private final List<Field<?>> trackedFields;

    public PlayerData(Field<?>... trackedFields) {
        this.trackedFields = List.of(trackedFields);
    }

    public void requestSave() {

    }

    /**
     * This is checked regularly to make sure a player's data is necessary to save.
     * @return
     */
    public boolean needSave() {
        return false;
    }

    /**
     * Field of a player's data
     * @param <T> Should only be primitive data type as well as their arrays and string
     */
    public static class Field<T> {
        private final AtomicReference<T> value = new AtomicReference<>();
        private boolean dirty;

        public void setValue(T value) {
            this.value.set(value);
        }

        public T getValue() {
            return value.get();
        }
    }
}
