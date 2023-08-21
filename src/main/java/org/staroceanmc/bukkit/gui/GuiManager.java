package org.staroceanmc.bukkit.gui;

import org.bukkit.entity.Player;
import org.staroceanmc.bukkit.AbstractPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This manager will maintain the lifecycle of managed Guis.
 */
public class GuiManager {

    private final AbstractPlugin plugin;
    private final Map<Player, GuiStack> displayStack = new HashMap<>();
    private final GuiActionListener actionListener = new GuiActionListener(this);

    public GuiManager(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    public void display(Player player, Gui<?, ?, ?> gui) {
        GuiStack stack = displayStack.get(player);

        if (stack == null) {
            stack = new GuiStack();
        }

        if (stack.peek() == gui) {
            throw new IllegalStateException("Trying to display an opened Gui to the same player");
        }

        stack.push(gui);

        gui.displayDirectly(player);
    }

    public void cleanup(Player player) {
        GuiStack stack = displayStack.remove(player);
        if (stack != null) {
            stack.destroyAll();
        }
    }

    public class GuiStack extends LinkedList<Gui<?, ?, ?>> {

        public void destroyAll() {
            if (this.size() == 0) {
                return;
            }

            Gui<?, ?, ?> gui;
            while ((gui = pop()) != null) {
                gui.pause();
                plugin.newTaskChain().async(gui::destroy).execute();
            }
        }
    }
}
