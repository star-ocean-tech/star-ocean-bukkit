package xianxian.mc.starocean.areaprotection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class AreaProtection extends Module {
    private FileConfiguration areaConfig;
    private final List<Area> loadedAreas = new ArrayList<Area>();
    private final List<Area> enabledAreas = new ArrayList<Area>();
    private Material selectTool;
    private SelectionListener selectionListener = new SelectionListener(this);

    public AreaProtection(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("对特定区域设置保护");
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        reload();
        plugin.getServer().getPluginManager().registerEvents(new ProtectionListener(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(selectionListener, plugin);

        CommandArea area = new CommandArea(this);
        plugin.getCommandManager().registerCommand(area);
    }

    @Override
    public void disable() {

    }

    public List<Area> getLoadedAreas() {
        return loadedAreas;
    }

    public List<Area> getEnabledAreas() {
        return enabledAreas;
    }

    public Area getEnabledAreaByName(String name) {
        for (int i = 0, size = enabledAreas.size(); i < size; i++) {
            Area a = enabledAreas.get(i);
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public Area getLoadedAreaByName(String name) {
        for (int i = 0, size = loadedAreas.size(); i < size; i++) {
            Area a = loadedAreas.get(i);
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public void addArea(Area area) {
        this.loadedAreas.add(area);
        save();
    }

    public void removeArea(Area area) {
        this.enabledAreas.remove(area);
        this.loadedAreas.remove(area);
        save();
    }

    public void enableArea(Area area) {
        this.enabledAreas.add(area);
        save();
    }

    public void disableArea(Area area) {
        this.disableArea(area);
        save();
    }

    public Material getSelectTool() {
        return selectTool;
    }

    public void setSelectTool(Material selectTool) {
        this.selectTool = selectTool;
    }

    public SelectionListener getSelectionListener() {
        return selectionListener;
    }

    @Override
    public void reload() {
        loadedAreas.clear();
        enabledAreas.clear();

        this.reloadConfig();
        FileConfiguration config = this.getConfig();
        String selectTool = config.getString("select-tool", Material.WOODEN_PICKAXE.name());
        try {
            this.selectTool = Material.valueOf(selectTool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.selectTool == null)
            this.selectTool = Material.WOODEN_PICKAXE;

        areaConfig = loadConfig("areas.yml");

        List<String> areaNames = areaConfig.getStringList("available-areas");
        List<String> enabledAreaNames = areaConfig.getStringList("enabled-areas");

        if (areaNames.size() == 0) {
            areaConfig.set("available-areas", Arrays.asList());
        }
        if (enabledAreaNames.size() == 0) {
            areaConfig.set("enabled-areas", Arrays.asList());
        }

        int areaLoaded = 0;
        long startTime = System.currentTimeMillis();

        for (String areaName : areaNames) {
            if (!areaConfig.isConfigurationSection(areaName)) {
                logger().severe("Unable to find area " + areaName);
                return;
            }

            ConfigurationSection section = areaConfig.getConfigurationSection(areaName);

            try {
                Area area = Area.readFrom(section);
                loadedAreas.add(area);
                if (enabledAreaNames.contains(areaName))
                    enabledAreas.add(area);
                areaLoaded++;
            } catch (NullPointerException e) {
                logger().severe("Unable to load area " + areaName);
                e.printStackTrace();
            }
        }

        logger().info("Loaded " + areaLoaded + " areas, cost " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void save() {
        List<String> availableAreas = new ArrayList<String>();
        List<String> enabledAreaNames = new ArrayList<String>();

        loadedAreas.forEach((a) -> {
            availableAreas.add(a.getName());
            ConfigurationSection section = areaConfig.getConfigurationSection(a.getName());
            if (section == null) {
                section = areaConfig.createSection(a.getName());
            }
            a.writeTo(section);
        });
        enabledAreas.forEach((a) -> enabledAreaNames.add(a.getName()));

        areaConfig.set("available-areas", availableAreas);
        areaConfig.set("enabled-areas", enabledAreaNames);

        getPlugin().newTaskChain()
            .async(()->saveConfig(areaConfig, "areas.yml"))
            .execute();
    }

    public Area getByPosition(Location location) {
        for (int i = 0, size = enabledAreas.size(); i < size; i++) {
            Area area = enabledAreas.get(i);
            if (area.isPositionIn(location))
                return area;
        }

        return null;
    }
}
