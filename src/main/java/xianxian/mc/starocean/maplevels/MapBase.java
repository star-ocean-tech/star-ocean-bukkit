package xianxian.mc.starocean.maplevels;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;

import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class MapBase {
    private String name;
    private MapManager parent;
    private MapLevels module;
    private World worldToCreate;
    private int baseLevelY;
    private Map<String, Location> absoluteLocations;
    private Map<String, Location> relativeLocations;
    private Map<String, Object> properties;
    private Clipboard clipboard;
    
    private MapBase() {
        
    }

    public World getWorld() {
        return worldToCreate;
    }
    
    public Clipboard getMapData() {
        return clipboard;
    }
    
    public int getBaseLevelY() {
        return baseLevelY;
    }
    
    public String getName() {
        return name;
    }

    public MapManager getParent() {
        return parent;
    }

    public Map<String, Location> getAbsoluteLocations() {
        return absoluteLocations;
    }

    public Map<String, Location> getRelativeLocations() {
        return relativeLocations;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public static class MapBuilder {
        private MapLevels module;
        private String name;
        private MapManager parent;
        private World worldToCreate;
        private int baseLevelY;
        private Clipboard clipboard;
        private Map<String, Location> absoluteLocations = new HashMap<String, Location>();
        private Map<String, Location> relativeLocations = new HashMap<String, Location>();
        private Map<String, Object> properties = new HashMap<>();
        
        public MapBuilder module(MapLevels module) {
            this.module = module;
            return this;
        }
        
        public MapBuilder parent(MapManager parent) {
            this.parent = parent;
            return this;
        }
        
        public MapBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public MapBuilder world(World world) {
            this.worldToCreate = world;
            return this;
        }
        
        public MapBuilder baseLevelY(int y) {
            this.baseLevelY = y;
            return this;
        }
        
        public MapBuilder clipboard(Clipboard clipboard) {
            this.clipboard = clipboard;
            return this;
        }
        
        public MapBuilder absoluteLocation(String name, Location location) {
            this.absoluteLocations.put(name, location);
            return this;
        }
        
        public MapBuilder relativeLocation(String name, Location location) {
            this.relativeLocations.put(name, location);
            return this;
        }
        
        public MapBuilder property(String name, Object object) {
            this.properties.put(name, object);
            return this;
        }
        
        public MapBase build() {
            MapBase map = new MapBase();
            map.name = name;
            map.parent = parent;
            map.module = module;
            map.worldToCreate = worldToCreate;
            map.clipboard = clipboard;
            map.baseLevelY = baseLevelY;
            map.absoluteLocations = absoluteLocations;
            map.relativeLocations = relativeLocations;
            map.properties = properties;
            return map;
        }
    }
}
