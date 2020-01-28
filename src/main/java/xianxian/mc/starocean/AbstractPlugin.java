package xianxian.mc.starocean;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractPlugin extends JavaPlugin {
    public abstract ModuleManager getModuleManager();

    public abstract PermissionManager getPermissionManager();

    public abstract CommandManager getCommandManager();

    public abstract DatabaseManager getDatabaseManager();
    
    public abstract ServerVersionMatcher getVersionMatcher();
}
