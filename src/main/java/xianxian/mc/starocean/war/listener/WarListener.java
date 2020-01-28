package xianxian.mc.starocean.war.listener;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDespawnEvent;
import xianxian.mc.starocean.war.StarOceanWar;

public class WarListener implements Listener {
    private final StarOceanWar module;
    
    public WarListener(StarOceanWar module) {
        this.module = module;
    }
    
    @EventHandler
    public void onMobDeath(MythicMobDeathEvent event) {
        module.getWarManager().mobDead(event.getMob());
    }
    
    @EventHandler
    public void onMobDespawn(MythicMobDespawnEvent event) {
        module.getWarManager().mobDead(event.getMob());
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        module.getWarManager().getOngoingWars().forEach((w)->{
            w.quit(event.getPlayer());
        });
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player || (event.getDamager() instanceof Arrow && ((Arrow)event.getDamager()).getShooter() instanceof Player))
            module.getWarManager().getOngoingWars().forEach((w)->{
                if (w.getBaseMob().getEntity().getBukkitEntity().equals(event.getEntity()))
                    event.setCancelled(true);
            });
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR))
            return;
    }
}
