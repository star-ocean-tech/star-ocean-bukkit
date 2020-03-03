package xianxian.mc.starocean;

import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

public abstract class AbstractPlugin extends JavaPlugin {
    public abstract ModuleManager getModuleManager();

    public abstract PermissionManager getPermissionManager();

    public abstract BukkitCommandManager getCommandManager();

    public abstract DatabaseManager getDatabaseManager();
    
    public abstract ServerVersionMatcher getVersionMatcher();
    
    public abstract GUIManager getGUIManager();
    
    public abstract TaskChainFactory getTaskChainFactory();
    
    public <T> TaskChain<T> newTaskChain() {
        return getTaskChainFactory().newChain();
    }
    
    public <T> TaskChain<T> newSharedTaskChain(String identifier) {
        return getTaskChainFactory().newSharedChain(identifier);
    }
}
