package org.staroceanmc.bukkit.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class GuiActionListener implements Listener {
    private final GuiManager manager;

    public GuiActionListener(GuiManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        manager.cleanup(event.getPlayer());
    }
}
