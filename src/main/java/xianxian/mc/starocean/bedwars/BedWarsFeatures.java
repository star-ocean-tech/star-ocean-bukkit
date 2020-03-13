package xianxian.mc.starocean.bedwars;

import org.bukkit.configuration.file.FileConfiguration;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class BedWarsFeatures extends Module {
    private boolean clearInventoryWhenDeath;
    private boolean dropResourcesWhenDeath;
    private boolean dropResourcesDirectlyToPlayer;

    public BedWarsFeatures(AbstractPlugin plugin) {
        super(plugin);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.getPlugin().getServer().getPluginManager().registerEvents(new DeathListener(this), plugin);
        
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reload() {
        reloadConfig();
        
        FileConfiguration config = getConfig();
        
        config.addDefault("clear-inventory-when-death", true);
        config.addDefault("drop-resources-when-death", true);
        config.addDefault("drop-resources-directly-to-player", true);
        
        saveConfig();
        
        clearInventoryWhenDeath = config.getBoolean("clear-inventory-when-death");
        dropResourcesWhenDeath = config.getBoolean("drop-resources-when-death");
        dropResourcesDirectlyToPlayer = config.getBoolean("drop-resources-directly-to-player");
    }

    public boolean isDropResourcesWhenDeath() {
        return dropResourcesWhenDeath;
    }

    public boolean isDropResourcesDirectlyToPlayer() {
        return dropResourcesDirectlyToPlayer;
    }

    public boolean isClearInventoryWhenDeath() {
        return clearInventoryWhenDeath;
    }

    
}
