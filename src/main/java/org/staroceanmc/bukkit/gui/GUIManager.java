package org.staroceanmc.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.staroceanmc.bukkit.AbstractPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

public class GUIManager implements Listener {
    private final AbstractPlugin plugin;
    private final Map<Player, GUIStack> guiStacks = new HashMap<>();
    private final Logger logger;
    private final GuiActionListener listener = new GuiActionListener(this);

    
    public GUIManager(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger(plugin.getName() + "-GUIManager");
    }
    
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void closeAll(Player player) {

    }

    public void closeCurrent(Player player) {

    }

    public void click() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.guiStacks.put(event.getPlayer(), new GUIStack());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GUIStack stack = this.guiStacks.remove(event.getPlayer());
        if (!stack.isEmpty()) {
            stack.forEach((gui)->gui.onDestroy());
            stack.clear();
        }
    }
    
    /**
     * Open a GUI for a player<br>
     * NOTE: Please don't call this as soon as a player join the server<br>
     * @param gui The GUI to display
     */
    public void open(GUI gui) {
        if (gui.isDestroyed())
            return;
        
        GUIStack stack = this.guiStacks.get(gui.getPlayer());
        GUI prevGui = stack.peek();
        if (prevGui != null) {
            prevGui.onPause();
        }
        this.guiStacks.get(gui.getPlayer()).offerFirst(gui);
        
        if (!gui.isCreated()) {
            plugin.newTaskChain()
                .asyncFirst(()->{
                    gui.onCreate();
                    return gui;
                })
                .sync(()->{
                    gui.onResume();
                    gui.getPlayer().openInventory(gui.getInventory());
                })
                .execute();
        } else {
            gui.onResume();
            gui.getPlayer().openInventory(gui.getInventory());
        }
    }
    
    public void close(GUI gui) {
        Player player = gui.getPlayer();
        
        GUIStack stack = this.guiStacks.get(player);
        if (stack != null) {
            stack.poll();
        }
        
        stack.remove(gui);
        gui.onDestroy();
        gui.setDestroyed(true);
        
        GUI prevGui = stack.peek();
        
        if (prevGui != null && prevGui.equals(gui)) {
            player.closeInventory();
            if (stack.isEmpty())
                return;
            GUI newGUI = stack.poll();
            if (newGUI != null) {
                open(newGUI);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (event.getInventory().getHolder() instanceof GUI) {
            GUI gui = (GUI) event.getInventory().getHolder();
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
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getReason().equals(Reason.PLAYER) || 
                event.getReason().equals(Reason.DEATH) || 
                event.getReason().equals(Reason.PLUGIN) || 
                event.getReason().equals(Reason.TELEPORT)) {
            if (event.getInventory().getHolder() instanceof GUI) {
                GUI gui = (GUI) event.getInventory().getHolder();
                gui.onPause();
                
                plugin.newTaskChain().async(gui::onDestroy).execute();
            }
        }
    }
    
    private static class GUIStack extends LinkedList<GUI> {

        /**
         * 
         */
        private static final long serialVersionUID = 5396482208795110890L;
    }
}
