package org.staroceanmc.bukkit;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.staroceanmc.bukkit.gui.GUIManager;
import org.staroceanmc.bukkit.gui.GuiManager;
import org.staroceanmc.bukkit.utils.ServerVersionMatcher;

public abstract class AbstractPlugin extends JavaPlugin {
    public abstract ModuleManager getModuleManager();

    public abstract PermissionManager getPermissionManager();

    public abstract BukkitCommandManager getCommandManager();

    public abstract DatabaseManager getDatabaseManager();
    
    public abstract ServerVersionMatcher getVersionMatcher();
    
    public abstract GUIManager getGUIManager();
    
    public abstract TaskChainFactory getTaskChainFactory();

    public abstract GuiManager getGuiManager();
    
    public <T> TaskChain<T> newTaskChain() {
        return getTaskChainFactory().newChain();
    }
    
    public <T> TaskChain<T> newSharedTaskChain(String identifier) {
        return getTaskChainFactory().newSharedChain(identifier);
    }
}
