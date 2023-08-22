package org.staroceanmc.bukkit.gui;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.staroceanmc.bukkit.Module;
import org.staroceanmc.bukkit.gui.view.View;

@Deprecated
public abstract class GUI implements InventoryHolder {
    private final Module module;
    private final Player player;
    private boolean destroyed;
    private final Table<Integer, Integer, View> views = HashBasedTable.create();
    private boolean created = false;
    
    public GUI(Module module, Player player) {
        this.module = module;
        this.player = player;
    }
    
    /**
     * Called when initialize the GUI, Will be called async
     */
    public abstract void onCreate();

    /**
     * Refresh the entire GUI. Regenerate all the items
     */
    public abstract void refresh();

    /**
     * Called when click the GUI's slot
     * @param event called the CUI
     */
    public void click(InventoryClickEvent event) {
        View view = views.get(event.getSlot() % 9, event.getSlot() / 9);
        view.click(event);
    }
    
    public void setSlot(int x, int y, View view) {
        views.column(y).put(x, view);
    }
    
    /**
     * Get the inventory of this GUI
     * @return inventory
     */
    public abstract Inventory getInventory();
    
    /**
     * @return The player who own the GUI
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Called when destory the GUI, Subclasses<br>
     * shouldn't call Player.closeInventory in this method
     */
    public abstract void onDestroy();
    
    public void onResume() {
        refresh();
    }
    
    public void onPause() {}
    
    public Module getModule() {
        return module;
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
    
    protected int[] calculatePosition(int index) {
        return new int[] { index % 9, index / 9 };
    }

    protected int calculateIndex(int x, int y) {
        return x + (9 * y);
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }
    
    
}
