package xianxian.mc.starocean.attack;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import xianxian.mc.starocean.AbstractPlugin;

public class InstantAttack extends xianxian.mc.starocean.Module implements Listener {
    private double attackSpeed;
    
    public InstantAttack(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("starocean.attack.instantattack"))
            event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AttributeInstance inst = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        inst.setBaseValue(inst.getDefaultValue());
    }

    @Override
    public void prepare() {
        reload();
        
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {
        reloadConfig();
        FileConfiguration config = getConfig();
        config.addDefault("generic.attackSpeed", 4);
        saveConfig();
        
        attackSpeed = config.getDouble("generic.attackSpeed", 4);
        
    }
    
}
