package org.staroceanmc.bukkit.gui;

import org.bukkit.entity.Player;
import org.staroceanmc.bukkit.AbstractPlugin;

import java.util.*;

/**
 * Handles the Gui history stack.
 */
@SuppressWarnings({"rawtypes"})
public class GuiManager {

    private final AbstractPlugin plugin;
    private final Map<Player, Gui> openedGuis = new HashMap<>();
    private final GuiActionListener actionListener = new GuiActionListener(this);

    public GuiManager(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(actionListener, plugin);
        plugin.getCommandManager().registerCommand(new CommandGui());
    }

    public boolean isDisplaying(Player player, Gui<?, ?, ?> gui) {
        return gui.equals(player.getOpenInventory().getTopInventory().getHolder());
    }

    public AbstractPlugin getPlugin() {
        return plugin;
    }

    public void close() {

    }

}
