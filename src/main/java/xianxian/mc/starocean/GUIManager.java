package xianxian.mc.starocean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GUIManager implements Listener {
    private AbstractPlugin plugin;
    private Map<Player, GUI> currentGuiMap = new HashMap<>();
    private Map<Player, GUIStack> guiStacks = new HashMap<>();
    private Logger logger;
    
    public GUIManager(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger(plugin.getName() + "-GUIManager");
    }
    
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.guiStacks.put(event.getPlayer(), new GUIStack());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GUIStack stack = this.guiStacks.remove(event.getPlayer());
        if (!stack.isEmpty()) {
            stack.forEach((gui)->gui.destroy());
            stack.clear();
        }
    }
    
    /**
     * Open a GUI for a player<br>
     * NOTE: Please don't call this as soon as a player join the server<br>
     * @param gui The GUI to display
     */
    public void open(GUI gui) {
        GUI prevGui = this.currentGuiMap.get(gui.getPlayer());
        if (prevGui != null)
            gui.getPlayer().closeInventory();
        this.currentGuiMap.put(gui.getPlayer(), gui);
        this.guiStacks.get(gui.getPlayer()).offerFirst(gui);
        gui.show();
    }
    
    public void close(GUI gui) {
        Player player = gui.getPlayer();
        GUI prevGui = this.currentGuiMap.get(player);
        GUIStack stack = this.guiStacks.get(player);
        stack.remove(gui);
        gui.destroy();
        gui.setDestroyed(true);
        if (prevGui != null && prevGui.equals(gui)) {
            this.currentGuiMap.remove(player);
            player.closeInventory();
            if (stack.isEmpty())
                return;
            GUI newGUI = stack.peek();
            this.currentGuiMap.put(player, newGUI);
            newGUI.show();
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        //logger.info(event.getClickedInventory() == null ? "null" : event.getClickedInventory().toString()+": "+event.getSlot());
        GUI gui = currentGuiMap.get(event.getWhoClicked());
        if (gui != null) {
            if (event.getClickedInventory() == null || !event.getClickedInventory().equals(gui.getInventory())) {
                if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT))
                    event.setCancelled(true);
                return;
            }
            
            event.setCancelled(true);
            gui.click(event);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        GUI gui = currentGuiMap.get(event.getWhoClicked());
        if (gui != null) {
            if (event.getInventory().equals(gui.getInventory())) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        currentGuiMap.values().forEach((gui)->{
            if (event.getDestination().equals(gui.getInventory())) {
                event.setCancelled(true);
                return;
            }
        });
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GUI gui = currentGuiMap.remove(event.getPlayer());
        if (gui != null) {
            close(gui);
        }
    }
    
    private static class GUIStack extends LinkedList<GUI> {
        
    }
}
