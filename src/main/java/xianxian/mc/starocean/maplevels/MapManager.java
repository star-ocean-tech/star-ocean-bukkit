package xianxian.mc.starocean.maplevels;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class MapManager {
    private final MapLevels module;
    private final World world;
    
    private int marginBetweenMaps = 512;
    
    private Table<Integer, Integer, MapCopy> constructedMaps = HashBasedTable.create();

    public MapManager(MapLevels module, World world) {
        this.module = module;
        this.world = world;
    }

    public MapCopy createCopy(MapBase base) {
        Random random = new Random();
        int x, z;
        boolean found = false;
        
        do {
            x = random.nextInt(1024);
            z = random.nextInt(1024);
            
            boolean flag1 = constructedMaps.contains(z, x);
            if (flag1) {
                x = -x;
                boolean flag2 = constructedMaps.contains(z, x);
                if (flag2) {
                    z = -z;
                    found = !constructedMaps.contains(z, x);
                }
            } else {
                found = true;
            }
        }
        while (!found);
        
        Location location = new Location(base.getWorld(), x * marginBetweenMaps, base.getBaseLevelY(), z * marginBetweenMaps);

        MapCopy copy = new MapCopy(this.module, location, base);
        
        this.constructedMaps.column(x).put(z, copy);
        
        return copy;
    }
}
