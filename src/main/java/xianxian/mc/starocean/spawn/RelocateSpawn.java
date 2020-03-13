package xianxian.mc.starocean.spawn;

import java.time.format.DateTimeFormatter;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class RelocateSpawn extends Module implements Listener {
    private Location entitySpawnLocation;
    private Location playerSpawnLocation;
    private FileConfiguration config;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RelocateSpawn(AbstractPlugin plugin) {
        super(plugin);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean checkIfCanLoad() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void prepare() {
        reload();
        
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        CommandRelocateSpawn rspawn = new CommandRelocateSpawn(this);
        this.getPlugin().getCommandManager().registerCommand(rspawn);
        this.getPlugin().getCommandManager().registerCommand(rspawn.new EntityCommand(this));
        this.getPlugin().getCommandManager().registerCommand(rspawn.new PlayerCommand(this));
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reload() {
        reloadConfig();
        config = getConfig();
        config.addDefault("entity-spawn", "not-set");
        config.addDefault("player-spawn", "not-set");
        saveConfig();
        
        if (config.isLocation("entity-spawn")) {
            setEntitySpawn(config.getLocation("entity-spawn"));
        }
        
        if (config.isLocation("player-spawn")) {
            setPlayerSpawn(config.getLocation("player-spawn"));
        }
    }
    
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getFrom().getWorld().getEnvironment().equals(Environment.THE_END) && event.getTo().getWorld().getEnvironment().equals(Environment.NORMAL)) {
            if (entitySpawnLocation != null) {
                event.setTo(entitySpawnLocation);
            }
        }
    }
    
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getFrom().getWorld().getEnvironment().equals(Environment.THE_END) && event.getTo().getWorld().getEnvironment().equals(Environment.NORMAL))
            if (playerSpawnLocation != null) {
                event.setTo(playerSpawnLocation);
            }
    }
    
    public void setEntitySpawn(Location location) {
        if (entitySpawnLocation != null)
            entitySpawnLocation.getChunk().removePluginChunkTicket(getPlugin());
        entitySpawnLocation = location;
        if (location == null) {
            config.set("entity-spawn", "not-set");
        } else {
            config.set("entity-spawn", location);
            entitySpawnLocation.getChunk().addPluginChunkTicket(getPlugin());
        }
    }
    public void setPlayerSpawn(Location location) {
        if (playerSpawnLocation != null)
            playerSpawnLocation.getChunk().removePluginChunkTicket(getPlugin());
        playerSpawnLocation = location;
        if (location == null) {
            config.set("player-spawn", "not-set");
        } else {
            config.set("player-spawn", location);
            playerSpawnLocation.getChunk().addPluginChunkTicket(getPlugin());
        }
        
    }
}
