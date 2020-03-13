package xianxian.mc.starocean.bedwars;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerKilledEvent;

public class DeathListener implements Listener {
    private BedWarsFeatures module;

    public DeathListener(BedWarsFeatures module) {
        this.module = module;
    }
    
    @EventHandler
    public void onPlayerDeath(BedwarsPlayerKilledEvent event) {
        if (module.isClearInventoryWhenDeath() && module.isDropResourcesWhenDeath() && module.isDropResourcesDirectlyToPlayer() && event.getDrops().size() != 0) {
            List<ItemStack> stacks = event.getGame().getItemSpawners().stream()
                    .map(((spawner)->spawner.getItemSpawnerType().getStack()))
                    .collect(Collectors.toList());
            stacks.forEach((stack)->{
                event.getDrops().stream().filter(stack::isSimilar).forEach((s)->event.getKiller().getInventory().addItem(s));
            });
            event.getDrops().clear();
        }
    }
}
