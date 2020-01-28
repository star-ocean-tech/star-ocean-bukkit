package xianxian.mc.starocean.missions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xianxian.mc.starocean.DatabaseConnectedEvent;

public class StorageListener implements Listener {
    private MissionsModule module;
    private MissionStorage storage;
    
    public StorageListener(MissionsModule module) {
        this.module = module;
        this.storage = module.getStorage();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        storage.load(event.getPlayer().getUniqueId().toString());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        storage.invalidateCache(event.getPlayer().getUniqueId().toString());
    }
    
    @EventHandler
    public void onDatabaseConnected(DatabaseConnectedEvent event) {
        storage.useDatabase(event.getDatabase());
    }
}
