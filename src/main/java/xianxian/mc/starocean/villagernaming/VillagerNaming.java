package xianxian.mc.starocean.villagernaming;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class VillagerNaming extends Module implements Listener {
    private List<String> firstNames = new ArrayList<String>();
    private List<String> secondNames = new ArrayList<String>();

    public VillagerNaming(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("每个新出生的村民都该有他自己的名字(滑稽");
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();
            villager.setCustomNameVisible(true);
            villager.setCustomName("");
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

}
