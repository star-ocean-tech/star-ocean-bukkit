package xianxian.mc.starocean.maplevels;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;

public class MapCopy {
    private MapLevels module;
    private Location baseLocation;
    private MapBase map;
    private EditSession session;
    private Region region;

    public MapCopy(MapLevels module, Location location, MapBase map) {
        this.module = module;
        this.baseLocation = location;
        this.map = map;
        this.session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(map.getWorld()), -1);
        Region orig = map.getMapData().getRegion();
        this.region = new CuboidRegion(BukkitAdapter.asBlockVector(baseLocation), BlockVector3.at(baseLocation.getX() + orig.getWidth(), baseLocation.getY() + orig.getHeight(), baseLocation.getZ() + orig.getLength()));
    }
    
    public MapBase getBase() {
        return map;
    }
    
    public Location getLocation() {
        return baseLocation;
    }
    
    public Location getAbsoluteLocation(String name) {
        return map.getAbsoluteLocations().get(name);
    }
    
    public Location getRelativeLocation(String name) {
        return map.getRelativeLocations().get(name).add(baseLocation);
    }
    
    public void clearArea() {
        try {
            this.session.setBlocks(region, BukkitAdapter.adapt(Bukkit.createBlockData(Material.AIR)));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
    
    public void create() {
        module.logger().info("New map created at " + baseLocation);
        map.getMapData().setOrigin(BukkitAdapter.asBlockVector(baseLocation));
        Operation operation = new ClipboardHolder(map.getMapData())
                .createPaste(session)
                .copyBiomes(true)
                .copyEntities(true)
                .to(BukkitAdapter.asBlockVector(baseLocation))
                .build();
        try {
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
        
    }
    
    public void destroy() {
        clearArea();
        session.close();
    }

}
