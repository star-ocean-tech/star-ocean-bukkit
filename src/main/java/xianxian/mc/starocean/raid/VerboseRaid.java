package xianxian.mc.starocean.raid;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class VerboseRaid extends Module implements Listener {

    public VerboseRaid(AbstractPlugin plugin) {
        super(plugin);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        logger().info("A raid has been triggered at " + event.getRaid().getLocation() + " by " + event.getPlayer().getDisplayName());
    }
    
    @EventHandler
    public void onRaidFinish(RaidFinishEvent event) {
        StringBuilder sb = new StringBuilder();
        Player[] players = event.getWinners().toArray(new Player[0]);
        for (int i = 0, size = players.length; i < size; i++) {
            if (i == size - 1) {
                sb.append(players[i].getDisplayName());
            } else {
                sb.append(players[i].getDisplayName()).append(", ");
            }
        }
        logger().info("A raid has finished at " + event.getRaid().getLocation() + " with winners: " + sb.toString());
    }
    
    @EventHandler
    public void onRaidStop(RaidStopEvent event) {
        logger().info("A raid has been stopped at " + event.getRaid().getLocation() + " because of " + event.getReason().name());
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
