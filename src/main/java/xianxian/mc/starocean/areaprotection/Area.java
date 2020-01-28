package xianxian.mc.starocean.areaprotection;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class Area {
    private String name;
    private Map<EnumEvent, AreaAction> actions = new EnumMap<EnumEvent, AreaAction>(EnumEvent.class);
    private List<String> playersBypassed = new ArrayList<String>();
    private String world;
    private Location locationFirst;
    private Location locationSecond;

    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;

    private Area() {

    }

    public Area(String name, String world, Location locationFirst, Location locationSecond) {
        this.name = name;
        this.world = world;
        this.locationFirst = locationFirst;
        this.locationSecond = locationSecond;

        for (EnumEvent action : EnumEvent.values()) {
            addAction(action, new AreaAction());
        }

        recalculatePosition();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPlayersBypassed() {
        return playersBypassed;
    }

    public void addPlayerBypassed(String player) {
        this.playersBypassed.add(player);
    }

    public Map<EnumEvent, AreaAction> getActions() {
        return actions;
    }

    public void addAction(EnumEvent action, AreaAction areaAction) {
        actions.put(action, areaAction);
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Location getLocationFirst() {
        return locationFirst;
    }

    public void setLocationFirst(Location locationFirst) {
        this.locationFirst = locationFirst;
    }

    public Location getLocationSecond() {
        return locationSecond;
    }

    public void setLocationSecond(Location locationSecond) {
        this.locationSecond = locationSecond;
    }

    public void recalculatePosition() {
        this.minX = Math.min(locationFirst.getBlockX(), locationSecond.getBlockX());
        this.maxX = Math.max(locationFirst.getBlockX(), locationSecond.getBlockX());
        this.minY = Math.min(locationFirst.getBlockY(), locationSecond.getBlockY());
        this.maxY = Math.max(locationFirst.getBlockY(), locationSecond.getBlockY());
        this.minZ = Math.min(locationFirst.getBlockZ(), locationSecond.getBlockZ());
        this.maxZ = Math.max(locationFirst.getBlockZ(), locationSecond.getBlockZ());
    }

    public boolean isPositionIn(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return isPositionIn(location.getWorld().getName(), x, y, z);
    }

    public boolean isPositionIn(String world, int x, int y, int z) {
        return (world.equals(this.world))
                && ((minX <= x && maxX >= x) && (minY <= y && maxY >= y) && (minZ <= z && maxZ >= z));
    }

    public void writeTo(ConfigurationSection section) {
        section.set("name", name);
        section.set("world", world);
        section.set("players-bypassed", playersBypassed);
        section.set("location-first", locationFirst);
        section.set("location-second", locationSecond);

        for (EnumEvent event : EnumEvent.values()) {
            AreaAction areaAction = actions.get(event);

            if (areaAction == null)
                areaAction = new AreaAction();

            section.set(event.codeName() + ".cancelled", areaAction.isCancelled());
            section.set(event.codeName() + ".executes", areaAction.getExecutes());
        }
    }

    /**
     * 
     * @param section
     * @return
     * @throws NullPointerException if any value in {@code section} is null
     */
    public static Area readFrom(ConfigurationSection section) throws NullPointerException {
        Area area = new Area();
        area.setName(Objects.requireNonNull(section.getString("name"), "name is null, Incomplete area settings"));
        area.setWorld(Objects.requireNonNull(section.getString("world"), "world is null, Incomplete area settings"));
        area.setLocationFirst(Objects.requireNonNull(section.getLocation("location-first"),
                "location-first is null, Incomplete area settings"));
        area.setLocationSecond(Objects.requireNonNull(section.getLocation("location-second"),
                "location-second is null, Incomplete area settings"));
        for (String player : section.getStringList("players-bypassed")) {
            area.addPlayerBypassed(player);
        }

        for (EnumEvent event : EnumEvent.values()) {
            ConfigurationSection eventSection;
            if (section.isConfigurationSection(event.codeName())) {
                eventSection = section.getConfigurationSection(event.codeName());
            } else {
                eventSection = section.createSection(event.codeName());
            }
            AreaAction action = new AreaAction();

            action.setCancelled(eventSection.getBoolean("cancelled"));
            if (eventSection.isList("executes"))
                action.setExecutes(eventSection.getStringList("executes"));

            area.addAction(event, action);
        }
        area.recalculatePosition();
        return area;
    }

    public static class AreaAction {
        private boolean cancelled;
        private List<String> executes = new ArrayList<String>();

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public List<String> getExecutes() {
            return executes;
        }

        public void setExecutes(List<String> executes) {
            this.executes = executes;
        }

    }

    public enum EnumEvent {
        ON_BLOCK_BREAK("on-block-break"), ON_BLOCK_PLACE("on-block-place");

        private final String codeName;

        private EnumEvent(String codeName) {
            this.codeName = codeName;
        }

        public String codeName() {
            return codeName;
        }
    }
}
