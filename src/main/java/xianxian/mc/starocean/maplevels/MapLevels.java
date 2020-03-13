package xianxian.mc.starocean.maplevels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.maplevels.MapBase.MapBuilder;

public class MapLevels extends Module {
    private Map<String, MapBase> maps = new HashMap<>();
    private Map<World, MapManager> managers = new HashMap<>();
    
    private boolean reloaded = false;

    public MapLevels(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit");
    }

    @Override
    public void prepare() {
        reload();
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void reload() {
        if (reloaded) {
            logger().severe("Reloading this module is prevented");
            return;
        }
        
        reloadConfig();
        reloaded = true;
        
        FileConfiguration config = getConfig();
        
        List<String> worldList = config.getStringList("map-worlds");
        for (String world : worldList) {
            World w = getPlugin().getServer().getWorld(world);
            
            managers.put(w, new MapManager(this, w));
        }
        
        File mapsDir = new File(getDataFolder(true), "maps");
        if (!mapsDir.exists())
            mapsDir.mkdir();
        if (!mapsDir.isDirectory()) {
            logger().severe("Maps directory is a file");
            File[] files = mapsDir.listFiles((f)->f.isDirectory());
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                File info = new File(file, "info.yml");
                if (info.exists()) {
                    FileConfiguration mapConfig = YamlConfiguration.loadConfiguration(info);
                    MapBuilder builder = new MapBuilder();
                    String name = file.getName();
                    World world = getPlugin().getServer().getWorld(mapConfig.getString("world"));
                    if (world == null) {
                        logger().severe("Map " + name + " requires a non-exist world");
                        continue;
                    }
                    MapManager manager = managers.get(world);
                    if (manager == null) {
                        logger().severe("Map " + name + " requires a world which is not usable, You need to specific the world in config.yml");
                        continue;
                    }
                    
                    builder.module(this)
                        .parent(manager)
                        .world(world)
                        .name(name)
                        .baseLevelY(mapConfig.getInt("base-level-y"));
                    
                    File schematicFile = new File(file, mapConfig.getString("schematic"));
                    if (!schematicFile.exists() || !schematicFile.isFile()) {
                        logger().severe("Map " + name + " requires a non-exist schematic file");
                        return;
                    }
                    
                    Clipboard clipboard;
                    
                    ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
                    
                    if (format == null) {
                        logger().severe("Map " + name + " requires an invalid schematic file");
                        return;
                    }
                    
                    try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                        clipboard = reader.read();
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    
                    builder.clipboard(clipboard);
                    
                    maps.put(name, builder.build());
                } else {
                    logger().severe(file.getName()+" doesn't contain a vaild info.yml");
                }
            }
        }
    }
    
    public MapBase getMap(String name) {
        return maps.get(name);
    }
}
