package org.staroceanmc.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wrapper of inventories to implement a simple GUI.
 * @param <T> Must be subclasses themselves.
 */
public abstract class Gui<T extends Gui<T, Inv>, Inv extends Inventory> implements InventoryHolder {
    protected List<Slot> slots;
    protected EventListener eventListener;
    private Inventory inventory;

    public Gui() {
        this.slots = createSlots();
    }

    /**
     * Actually creates related objects and underlying inventory.
     * This must be invoked first before any other actions.
     */
    public abstract T create();
    public abstract void display();

    public T pos(int index, ItemStack stack) {
        return pos(index, stack, null);
    }

    public abstract T pos(int index, ItemStack stack, @Nullable OnClick onClick);

    protected void setSlot(int index, ItemStack item) {

    }

    public T listener(EventListener listener) {
        this.eventListener = listener;
        return (T) this;
    }

    @NotNull
    protected abstract List<Slot> createSlots();

    @NotNull
    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            throw new IllegalStateException("getInventory called before creating");
        }

        return inventory;
    }

    protected class Slot {
        private ItemStack item;
        private OnClick onClick;

        public void setItem(ItemStack item) {
            this.item = item;
        }

        @Nullable
        public ItemStack getItem() {
            return item;
        }

        public boolean click(Player player, ClickType clickType) {
            if (onClick != null) {
                return onClick.onClick(player, clickType);
            }

            return false;
        }
    }

    /**
     * A default GUI implementation using vanilla chest inventory.
     */
    public static class Default extends Gui<Default, Inventory> {
        private final int row;

        public Default(int row) {
            this.row = row;
        }

        @Override
        public Default create() {

            return null;
        }

        @Override
        public void display() {

        }

        @Override
        public Default pos(int index, ItemStack stack, @Nullable OnClick onClick) {
            return null;
        }

        @Override
        protected @NotNull List<Gui<Default, Inventory>.Slot> createSlots() {
            return null;
        }

        public Default pos(int x, int y, ItemStack stack) {
            return null;
        }

        public Default pos(int x, int y, ItemStack stack, OnClick onClick) {
            return null;
        }

        public Default divideLineVertical(int x) {
            return this;
        }

        public Default divideLineHorizontal(int x) {
            return this;
        }
    }

    public static class Anvil {

    }

    public interface OnClick {

        /**
         * Invoked when a player clicked a slot.
         * @return True if player can move item in this slot.
         */
        boolean onClick(Player player, ClickType type);
    }

    public interface EventListener {

        void onDisplay();

        void onPause();

        void onDestroy();
    }
}
