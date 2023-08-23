package org.staroceanmc.bukkit.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class GuiActionListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("StarOcean-Gui");
    private final GuiManager manager;

    public GuiActionListener(GuiManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Gui<?,?,?> gui && event.getView().getPlayer() instanceof Player player) {
            event.setCancelled(!gui.click(player, event.getSlot(), event.getClick()));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Gui<?,?,?> gui && event.getView().getPlayer() instanceof Player player) {
            gui.onClose(player);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Gui<?,?,?> gui && event.getView().getPlayer() instanceof Player player) {
            gui.onDisplay(player);
        }
    }
}
