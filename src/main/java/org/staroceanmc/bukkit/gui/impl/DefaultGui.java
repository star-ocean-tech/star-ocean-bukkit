package org.staroceanmc.bukkit.gui.impl;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.staroceanmc.bukkit.gui.Gui;
import org.staroceanmc.bukkit.gui.GuiManager;

/**
 * A default GUI implementation using vanilla chest inventory.
 */
public class DefaultGui<Holder> extends Gui<DefaultGui<Holder>, Inventory, Holder> {
    private final int row;

    public DefaultGui(GuiManager manager, int row) {
        super(manager);
        if (row < 0 || row > 6) {
            throw new IllegalArgumentException("Unsupported Gui rows: " + row);
        }
        this.row = row;
    }

    @Override
    public DefaultGui<Holder> create0() {
        if (getTitle() != null) {
            setInventory(Bukkit.createInventory(this, row * 9, getTitle()));
        } else {
            setInventory(Bukkit.createInventory(this, row * 9));
        }

        createSlots();
        return this;
    }

    public DefaultGui<Holder> pos(int x, int y, ItemStack stack) {
        return pos(x + (y * 9), stack);
    }

    /**
     * Changes the item at specified position.
     *
     * @param x       Starts 0
     * @param y       Starts 0
     * @param stack   The item to display
     * @param onClick Called when the slot is clicked
     * @return
     */
    public DefaultGui<Holder> pos(int x, int y, ItemStack stack, OnClick onClick) {
        return pos(x + (y * 9), stack, onClick);
    }

    /**
     * Vertically raw a divide line with an item.
     *
     * @param x    Starts 0
     * @param item The item to display
     * @return
     */
    public DefaultGui<Holder> divideLineVertical(int x, ItemStack item) {
        for (int y = 0; y < row; y++) {
            pos(x, y, item);
        }

        return this;
    }

    /**
     * Horizontally raw a divide line with an item.
     *
     * @param y    Starts 0
     * @param item The item to display
     * @return
     */
    public DefaultGui<Holder> divideLineHorizontal(int y, ItemStack item) {
        for (int x = 0; x < 9; x++) {
            pos(x, y, item);
        }

        return this;
    }
}
