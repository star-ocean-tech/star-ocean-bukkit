package xianxian.mc.starocean.minigames.tnttag.sign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.minigames.tnttag.Game;
import xianxian.mc.starocean.minigames.tnttag.PlayerContext;
import xianxian.mc.starocean.minigames.tnttag.TNTTag;
import xianxian.mc.starocean.minigames.tnttag.PlayerContext.PlayerState;

public class SignManager implements Listener {
    private final TNTTag module;
    private List<SignLocation> signLocations = new ArrayList<>();
    private File file;
    
    static {
        ConfigurationSerialization.registerClass(SignLocation.class);
    }

    public SignManager(TNTTag module, File file) {
        this.module = module;
        this.file = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.isList("signs")) {
            List<SignLocation> locations = (List<SignLocation>) config.getList("signs", new ArrayList<SignLocation>());
            signLocations.addAll(locations);
        }
    }
    
    public void save() {
        FileConfiguration config = new YamlConfiguration();
        config.set("signs", new ArrayList<SignLocation>(signLocations));
        module.getPlugin().newTaskChain().async(()->{
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).execute();
    }
    
    public void update(Game game) {
        for (SignLocation location : signLocations) {
            if (!location.getName().equals(game.getName()))
                continue;
            Block block = location.getLocation().getBlock();
            if (block != null && block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                sign.setLine(0, "" + ChatColor.RED + ChatColor.BOLD + "[TNTTag]");
                if (location.getType().equals("leave")) {
                    
                } else if (location.getType().equals("join")) {
                    sign.setLine(1, ChatColor.RED + "右键加入");
                    sign.setLine(2, location.getName());
                    if (!game.isStarted())
                        sign.setLine(3, "" + ChatColor.YELLOW + game.getPlayers().size() + "/" + game.getMaxPlayers());
                    else
                        sign.setLine(3, ChatColor.YELLOW + "进行中");
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign && event.getHand().equals(EquipmentSlot.HAND)) {
            SignLocation loc = getByLocation(event.getClickedBlock().getLocation());
            if (loc != null) {
                if (loc.getType().equalsIgnoreCase("leave")) {
                    PlayerContext context = PlayerContext.fromPlayer(event.getPlayer());
                    Game game = context.getCurrentGame();
                    if (game != null && (!game.isStarted() || context.getState().equals(PlayerState.DEAD)))
                        game.quit(context);
                } else if (loc.getType().equalsIgnoreCase("join")) {
                    PlayerContext context = PlayerContext.fromPlayer(event.getPlayer());
                    if (context.getCurrentGame() != null) {
                        return;
                    }
                    String name = loc.getName();
                    Game game = module.getGame(name);
                    if (game != null) {
                        game.join(event.getPlayer());
                    }
                } else if (loc.getType().equalsIgnoreCase("ready")) {
                    PlayerContext context = PlayerContext.fromPlayer(event.getPlayer());
                    Game game = context.getCurrentGame();
                    if (game != null && !game.isStarted())
                        game.toggleReady(context);
                }
            }
        }
    }
    
    public SignLocation getByLocation(Location location) {
        for (int i = 0, size = signLocations.size(); i < size; i++) {
            SignLocation signLocation = signLocations.get(i);
            if (location.equals(signLocation.getLocation())) {
                return signLocation;
            }
        }
        return null;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            Location location = event.getBlock().getLocation();
            for (int i = 0, size = signLocations.size(); i < size; i++) {
                SignLocation signLocation = signLocations.get(i);
                if (location.equals(signLocation.getLocation())) {
                    this.signLocations.remove(signLocation);
                }
            }
        }
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[TNTTag]") && event.getPlayer().hasPermission("starocean.tnttag.createsign") && event.getBlock().getState() instanceof Sign) {
            event.setLine(0, "" + ChatColor.RED + ChatColor.BOLD + "[TNTTag]");
            String type = event.getLine(1);
            String name = event.getLine(2);
            if (type.isEmpty()) {
                module.getMessager().sendMessageTo(event.getPlayer(), ChatColor.RED + "未指定牌子类型");
                return;
            }
            if (type.equalsIgnoreCase("join")) {
                Game game = module.getGame(name);
                if (game == null) {
                    module.getMessager().sendMessageTo(event.getPlayer(), ChatColor.RED + "牌子信息无效");
                    return;
                }
                event.setLine(1, ChatColor.RED + "右键加入");
                event.setLine(2, name);
                if (!game.isStarted())
                    event.setLine(3, "" + ChatColor.YELLOW + game.getPlayers().size() + "/" + game.getMaxPlayers());
                else
                    event.setLine(3, ChatColor.YELLOW + "进行中");
            } else if (type.equalsIgnoreCase("leave")) {
                event.setLine(1, ChatColor.RED + "右键离开");
            } else if (type.equalsIgnoreCase("ready")) {
                event.setLine(1, ChatColor.RED + "右键切换准备状态");
            }
            Location location = event.getBlock().getLocation();
            SignLocation signLocation = new SignLocation(name, type, location);
            this.signLocations.add(signLocation);
            save();
        }
    }
    
    public static class SignLocation implements ConfigurationSerializable {
        private String name;
        private String type;
        private Location location;
        
        public SignLocation(String name, String type, Location location) {
            super();
            this.name = name;
            this.type = type;
            this.location = location;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Location getLocation() {
            return location;
        }
        
        public void setLocation(Location location) {
            this.location = location;
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> config = new HashMap<String, Object>();
            config.put("name", name);
            config.put("type", type);
            config.put("location", location);
            return config;
        }
        
        public static SignLocation deserialize(Map<String, Object> args) {
            return new SignLocation((String) args.get("name"), (String) args.get("type"), (Location) args.get("location"));
        }
    }
}
