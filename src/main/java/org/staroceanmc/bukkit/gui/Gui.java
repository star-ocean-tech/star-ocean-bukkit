package org.staroceanmc.bukkit.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wrapper of inventories to implement a simple GUI.
 * Provides utility methods intended for chained call.
 * @param <T> Must be subclasses themselves.
 */
public abstract class Gui<T extends Gui<T, Inv, Holder>,
        Inv extends Inventory,
        Holder>
        implements InventoryHolder {
    protected List<Slot> slots;
    protected EventListener<T, Inv, Holder> eventListener;
    private Inv inventory;
    private WeakReference<Holder> holder;
    private Component title;
    private final AtomicBoolean destroyed = new AtomicBoolean();
    private final AtomicBoolean created = new AtomicBoolean();

    public Gui() {

    }


    /**
     * Called before {@link Gui#create()} to specify a title for this Gui
     */
    public T title(Component title) {
        this.title = title;
        return (T) this;
    }

    /**
     * Actually creates related objects and underlying inventory.
     * This must be invoked first before any other actions involves inventory content alternation.
     * The process of creation can be executed async.
     */
    public T create() {
        if (created.get()) {
            throw new IllegalStateException("create called on an created Gui");
        }
        created.set(true);

        return create0();
    }

    protected abstract T create0();

    public void destroy() {
        if (destroyed.get()) {
            throw new IllegalStateException("destroy called on an destroyed Gui");
        }
        destroyed.set(true);

        if (eventListener != null) {
            eventListener.onDestroy(this);
        }
    }

    public void close() {

    }
    
    public void pause() {
        
    }
    
    public void resume() {
        
    }

    /**
     * Directly opens the Gui without adding it to display stack.
     * See also: {@link GuiManager#display(Player, Gui)}
     */
    public void displayDirectly(Player player) {
        if (inventory == null) {
            throw new IllegalStateException("display called before this Gui gets created");
        }

        if (eventListener != null) {
            eventListener.onDisplay(this);
        }

        player.openInventory(getInventory());
    }


    public T pos(int index, ItemStack stack) {
        return pos(index, stack, null);
    }

    public T pos(int index, ItemStack stack, @Nullable OnClick onClick) {
        setSlot(index, stack, onClick);
        return (T) this;
    }

    public T holder(Holder holder) {
        this.holder = new WeakReference<>(holder);
        return (T) this;
    }

    @Nullable
    public Holder getHolder() {
        return holder == null ? null : holder.get();
    }

    protected void setSlot(int index, ItemStack item, @Nullable OnClick onClick) {
        if (inventory == null) {
            throw new IllegalStateException("setSlot called before this Gui gets created");
        }

        if (index >= inventory.getSize()) {
            throw new IndexOutOfBoundsException(index);
        }

        inventory.setItem(index, item);

        Slot slot = slots.get(index);

        slot.item = item;
        slot.onClick = onClick;
    }

    protected void createSlots() {
        this.slots = new ArrayList<>(inventory.getSize());
        for (int i = 0; i < inventory.getSize(); i++) {
            slots.add(new Slot());
        }
    }

    public T listener(EventListener<T, Inv, Holder> listener) {
        this.eventListener = listener;
        return (T) this;
    }

    @NotNull
    protected Slot getSlot(int index) {
        return slots.get(index);
    }

    @Nullable
    protected Component getTitle() {
        return title;
    }

    @NotNull
    @Override
    public Inv getInventory() {
        if (inventory == null) {
            throw new IllegalStateException("getInventory called before this Gui gets created");
        }

        return inventory;
    }

    protected void setInventory(@NotNull Inv inventory) {
        this.inventory = inventory;
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
    public static class Default<Holder> extends Gui<Default<Holder>, Inventory, Holder> {
        private final int row;

        public Default(int row) {
            this.row = row;
        }

        @Override
        public Default<Holder> create0() {
            if (getTitle() != null) {
                setInventory(Bukkit.createInventory(this, row * 9, getTitle()));
            } else {
                setInventory(Bukkit.createInventory(this, row * 9));
            }

            createSlots();
            return this;
        }

        public Default<Holder> pos(int x, int y, ItemStack stack) {
            return null;
        }

        /**
         * Changes the item at specified position.
         *
         * @param x Starts 0
         * @param y Starts 0
         * @param stack The item to display
         * @param onClick Called when the slot is clicked
         * @return
         */
        public Default<Holder> pos(int x, int y, ItemStack stack, OnClick onClick) {
            return null;
        }

        /**
         * Vertically raw a divide line with an item.
         *
         * @param x Starts 0
         * @param item The item to display
         * @return
         */
        public Default<Holder> divideLineVertical(int x, ItemStack item) {
            return this;
        }

        /**
         * Horizontally raw a divide line with an item.
         *
         * @param y Starts 0
         * @param item The item to display
         * @return
         */
        public Default<Holder> divideLineHorizontal(int y, ItemStack item) {
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

    public interface EventListener<T extends Gui<T, Inv, Holder>, Inv extends Inventory, Holder> {

        /**
         * Called just before a Gui is shown to player for the first time.
         */
        void onDisplay(Gui<T, Inv, Holder> gui);

        /**
         * Called when a Gui resumes from background and reopens to player.
         */
        void onResume(Gui<T, Inv, Holder> gui);

        /**
         * Called when a Gui is hide but not closed and can be resumed later, e.g. a new Gui opened.
         */
        void onPause(Gui<T, Inv, Holder> gui);

        /**
         * Called when
         */
        void onDestroy(Gui<T, Inv, Holder> gui);
    }
}
