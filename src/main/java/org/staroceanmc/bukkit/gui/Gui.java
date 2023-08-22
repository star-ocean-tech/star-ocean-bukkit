package org.staroceanmc.bukkit.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.staroceanmc.bukkit.gui.impl.DefaultGui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Wrapper of inventories to implement a simple GUI.
 * Provides utility methods intended for chained call.
 * <p>
 * Multiple viewer support: A Gui can be opened to multiple players.
 * When there is no viewers, this Gui will pause.
 * When there is no owners, this Gui will be destroyed.
 * </p>
 * See {@link DefaultGui} for examples.
 * @param <T> Must be subclasses themselves.
 */
public abstract class Gui<T extends Gui<T, Inv, Holder>,
        Inv extends Inventory,
        Holder>
        implements InventoryHolder {

    public static final int FLAG_NO_HISTORY_STACK = 1;
    public static final int FLAG_PERSISTENT = 1 << 1;

    private static final Logger LOGGER = Logger.getLogger("StarOcean-Gui");

    private final AtomicBoolean destroyed = new AtomicBoolean();
    private final AtomicBoolean created = new AtomicBoolean();
    private final Set<Player> owners = new HashSet<>();
    private final Set<Player> viewers = new HashSet<>();
    private final GuiManager manager;

    protected List<Slot> slots;
    protected EventListener<T, Inv, Holder> eventListener;
    private Inv inventory;
    private WeakReference<Holder> holder;
    private Component title;
    private int flags;

    // TODO: May associate with multiple players

    public Gui(GuiManager manager) {
        this.manager = manager;
    }

    /**
     * Called before {@link Gui#create()} to specify a title for this Gui.
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

    public int getFlags() {
        return flags;
    }

    public T flags(int flags) {
        this.flags = flags;
        return (T) this;
    }

    protected abstract T create0();

    public void display(Player player) {
        displayDirectly(player);
    }

    public void destroy() {
        if (destroyed.get()) {
            throw new IllegalStateException("destroy called on an destroyed Gui");
        }
        destroyed.set(true);

        if (eventListener != null) {
            try {
                eventListener.onDestroy(this);
            } catch (Throwable e) {
                LOGGER.severe("Error occurred while processing event");
                e.printStackTrace();
            }
        }
    }

    public void close(Player player) {
        player.closeInventory(InventoryCloseEvent.Reason.PLAYER);
    }

    /*
    public void resume(Player player) {
        if (owners.contains(player)) {
            displayDirectly(player);
        }
    }

     */

    /**
     * Called when a player closes a Gui.
     */
    public void onClose(Player player) {
        viewers.remove(player);
        owners.remove(player);

        if (eventListener != null) {
            try {
                eventListener.onClose(this, player);
            } catch (Throwable e) {
                LOGGER.severe("Error occurred while processing event");
                e.printStackTrace();
            }
        }

        if (this.owners.isEmpty() && (flags & FLAG_PERSISTENT) == 0) {
            destroy();
        }
    }

    /*
    void onPause(Player player) {
        viewers.remove(player);

        if (eventListener != null) {
            try {
                eventListener.onPause(this, player);
            } catch (Throwable e) {
                LOGGER.severe("Error occurred while processing event");
                e.printStackTrace();
            }
        }
    }
     */

    public boolean click(Player player, int index, ClickType clickType) {
        return getSlot(index).click(player, clickType);
    }

    void displayDirectly(Player player) {
        if (inventory == null) {
            throw new IllegalStateException("display called before this Gui gets created");
        }

        if (!owners.contains(player)) {
            owners.add(player);
        }

        player.openInventory(getInventory());
    }


    public void onDisplay(Player player) {
        viewers.add(player);

        if (eventListener != null) {
            try {
                eventListener.onDisplay(this, player);
            } catch (Throwable e) {
                LOGGER.severe("Error occurred while processing event");
                e.printStackTrace();
            }
        }
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

    /**
     * Gets the underlying inventory associated with this Gui.
     * Displaying should go through GuiManager or issues can occur.
     * @return The inventory created by this Gui.
     */
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

    /**
     * @return Players who owned this Gui (but not necessarily watching it).
     */
    public Set<Player> getOwners() {
        return owners;
    }

    /**
     * @return Players who are looking at this Gui.
     */
    public Set<Player> getViewers() {
        return viewers;
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

    public static class MultiPagedDefault<Holder> extends DefaultGui<Holder> {
        private final List<PagePane> pages = new ArrayList<>();
        private final List<Slot> allSlots = new ArrayList<>();

        public MultiPagedDefault(GuiManager manager, int row) {
            super(manager, row);
        }

        public PagePane addMultiPageArea(int fromX, int fromY, int toX, int toY) {
            return null;
        }

        public MultiPagedDefault<Holder> addItem() {
            return this;
        }

        public class PagePane {

        }
    }

    public static class Anvil<Holder> extends Gui<Anvil<Holder>, AnvilInventory, Holder> {

        public Anvil(GuiManager manager) {
            super(manager);
        }

        @Override
        protected Anvil<Holder> create0() {

            return null;
        }
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
         * Called before a Gui is shown to player.
         */
        void onDisplay(Gui<T, Inv, Holder> gui, Player player);

        /**
         * Called when a Gui is placed in background but not closed and can be resumed later, e.g. when a new Gui opened by this Gui.
         */
        //void onPause(Gui<T, Inv, Holder> gui, Player player);

        void onClose(Gui<T, Inv, Holder> gui, Player player);

        /**
         * Called when a Gui has no viewer and should be destroyed.
         */
        void onDestroy(Gui<T, Inv, Holder> gui);
    }
}
