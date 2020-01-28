package xianxian.mc.starocean.missions;

import org.bukkit.event.Listener;

import me.clip.placeholderapi.PlaceholderAPI;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.placeholderapi.PlaceHolderAPIFeatures;

public class MissionsModule extends Module implements Listener {
    private MissionStorage storage = new MissionStorage(this);

    public MissionsModule(AbstractPlugin plugin) {
        super(plugin);
        this.setName("Missions");
        this.setDescription("提供任务支持");
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        
        try {
            if (getPlugin().getModuleManager().isModuleLoaded(PlaceHolderAPIFeatures.class)) {
                PlaceholderAPI.registerExpansion(new ModulePlaceholderExpansion(this));
            }
        } catch (LinkageError e) {
            logger().severe("PlaceholderAPI not found!");
        }
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void reload() {
        
    }

    public MissionStorage getStorage() {
        return storage;
    }
}
