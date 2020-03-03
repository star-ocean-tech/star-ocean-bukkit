package xianxian.mc.starocean;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class GUI {
    private final Module module;
    private final Player player;
    private boolean destroyed;
    
    public GUI(Module module, Player player) {
        this.module = module;
        this.player = player;
    }
    
    /**
     * Called when initialize the GUI
     */
    public abstract void prepare();

    /**
     * Refresh the entire GUI
     */
    public abstract void refresh();

    /**
     * Called when click the GUI's slot
     * @param event called the CUI
     */
    public abstract void click(InventoryClickEvent event);
    
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
    
    public void show() {
        refresh();
        player.openInventory(getInventory());
    }
    
    /**
     * Called when destory the GUI, Subclasses<br>
     * shouldn't call Player.closeInventory in this method
     */
    public abstract void destroy();
    
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
}
