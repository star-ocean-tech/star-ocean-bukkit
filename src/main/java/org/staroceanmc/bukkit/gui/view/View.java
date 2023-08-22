package org.staroceanmc.bukkit.gui.view;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated
public abstract class View {
    private ItemStack displayItem;
    
    public abstract void click(InventoryClickEvent event);

}
