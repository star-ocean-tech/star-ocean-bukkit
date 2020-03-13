package xianxian.mc.starocean.vehicles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class DismountWhenDisconnect extends Module implements Listener {

    public DismountWhenDisconnect(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getVehicle() != null)
            event.getPlayer().getVehicle().removePassenger(event.getPlayer());
    }

    @Override
    public void prepare() {
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reload() {
        // TODO Auto-generated method stub

    }

}
