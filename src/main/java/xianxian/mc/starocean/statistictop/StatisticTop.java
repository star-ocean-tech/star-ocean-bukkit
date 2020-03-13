package xianxian.mc.starocean.statistictop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.scoreboard.ScoreboardScreen;

public class StatisticTop extends Module implements Listener {
    public static final Comparator<PlayerData> COMPARATOR = new Comparator<PlayerData>() {
        @Override
        public int compare(PlayerData o1, PlayerData o2) {
            return o2.getValue() - o1.getValue();
        };
    };
    
    private Map<Player, StatisticTopScreen> statisticTopScreens = new HashMap<>();
    private List<PlayerData> statisticTop;
    private List<PlayerData> playersToDisplay;
    
    private String title;
    private Statistic statistic;
    private int displayCount;
    
    private FileConfiguration playerdata;
    private final ScoreboardScreen screenModule = getPlugin().getModuleManager().getLoadedModule(ScoreboardScreen.class);

    private boolean dirty;
    
    private AtomicBoolean booleanIsRefreshing = new AtomicBoolean(false);
    
    public StatisticTop(AbstractPlugin plugin) {
        super(plugin);
        this.statisticTop = new ArrayList<PlayerData>();
        this.playersToDisplay = new ArrayList<PlayerData>();
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        PlayerData data = getPlayerDataByUUID(uuid);
        if (data == null) {
            data = newPlayer(event.getPlayer());
        }
        
        if (data.needDisplayStatisticBoard()) {
            StatisticTopScreen screen = new StatisticTopScreen(this, screenModule, event.getPlayer());
            screenModule.setScreen(event.getPlayer(), screen);
            this.statisticTopScreens.put(event.getPlayer(), screen);
            screen.show();
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.statisticTopScreens.remove(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (event.isCancelled())
            return;
        
        if (event.getStatistic().equals(statistic)) {
            PlayerData data = getPlayerDataByUUID(event.getPlayer().getUniqueId().toString());
            if (data == null) {
                newPlayer(event.getPlayer());
            } else {
                data.setValue(data.getValue() + (event.getNewValue() - event.getPreviousValue()));
                data.setDirty(true);
                setDirty(true);
            }
        }
    }

    @Override
    public void prepare() {
        reload();
        
        this.getPlugin().getServer().getPluginManager().registerEvents(this, this.getPlugin());
        
        CommandStatsTop statstop = new CommandStatsTop(this);
        this.getPlugin().getCommandManager().registerCommand(statstop);

        BukkitRunnable runnable = new BukkitRunnable() {
            
            @Override
            public void run() {
                statisticTop.forEach((data)->{
                    if (data.isDirty())
                        saveData(data);
                });
                saveConfig(playerdata, "playerdata.yml");
            }
        };
        
        runnable.runTaskTimerAsynchronously(this.getPlugin(), 1200, 1200);
    }

    @Override
    public void disable() {
        this.statisticTop.forEach(this::saveData);
        saveConfig(playerdata, "playerdata.yml");
    }

    @Override
    public void reload() {
        FileConfiguration moduleConfig = getConfig();
        moduleConfig.addDefault("scoreboard.title", "Mine block TOP15");
        moduleConfig.addDefault("statistic.type", Statistic.MINE_BLOCK.name());
        moduleConfig.addDefault("statistic.display-count", 10);
        
        this.title = moduleConfig.getString("scoreboard.title", "Mine block TOP15");
        this.statistic = Statistic.valueOf(moduleConfig.getString("statistic.type", Statistic.MINE_BLOCK.name()));
        this.displayCount = moduleConfig.getInt("statistic.display-count", 10);
        
        saveConfig();
        
        if (this.statistic == null)
            this.statistic = Statistic.MINE_BLOCK;
        
        playerdata = loadConfig("playerdata.yml");
        long startTime = System.currentTimeMillis();
        this.statisticTop.clear();
        playerdata.getKeys(false).forEach((key)->{
            ConfigurationSection section = playerdata.getConfigurationSection(key);
            if (section == null)
                return;
            String uuid = key;
            String displayName = section.getString("display-name");
            int value = section.getInt("value");
            boolean visibility = section.getBoolean("visibility");
            boolean displayStatisticBoard = section.getBoolean("display-statistic-board");
            
            PlayerData data = new PlayerData(uuid, displayName, value, visibility, displayStatisticBoard);
            this.statisticTop.add(data);
        });
        this.statisticTop.sort(COMPARATOR);
        logger().info(String.format("Loaded %d data into memory, cost %dms", this.statisticTop.size(), (int) (System.currentTimeMillis() - startTime)));
    }

    /**
     * Toggle visibility for a player
     * @param player
     * @return new value
     */
    public boolean toggleVisibility(Player player) {
        PlayerData data = getPlayerDataByUUID(player.getUniqueId().toString());
        if (data != null) {
            boolean visibility = !data.isVisible();
            data.setVisible(visibility);
            data.setDirty(true);
            return visibility;
        }
        return false;
    }
    
    /**
     * Toggle statistic board visibility for a player
     * @param player
     * @return new value
     */
    public boolean toggleNeedDisplayStatisticBoard(Player player) {
        PlayerData data = getPlayerDataByUUID(player.getUniqueId().toString());
        if (data != null) {
            boolean needDisplayStatisticBoard = !data.needDisplayStatisticBoard();
            data.setDisplayStatisticBoard(needDisplayStatisticBoard);
            data.setDirty(true);
            if (needDisplayStatisticBoard) {
                StatisticTopScreen screen = new StatisticTopScreen(this, screenModule, player);
                screenModule.setScreen(player, screen);
                this.statisticTopScreens.put(player, screen);
                screen.show();
            } else {
                screenModule.resetToDefaultScreen(player);
                this.statisticTopScreens.remove(player);
            }
            return needDisplayStatisticBoard;
        }
        return false;
    }
    
    public PlayerData getPlayerDataByUUID(String uuid) {
        for (int i = 0, size = this.statisticTop.size(); i < size; i++) {
            PlayerData data = this.statisticTop.get(i);
            if (data.getUuid().equals(uuid))
                return data;
        }
        return null;
    }
    
    public void saveData(PlayerData data) {
        String uuid = data.getUuid();
        this.playerdata.set(uuid+".display-name", data.getDisplayName());
        this.playerdata.set(uuid+".value", data.getValue());
        this.playerdata.set(uuid+".visibility", data.isVisible());
        this.playerdata.set(uuid+".display-statistic-board", data.needDisplayStatisticBoard());
        data.setDirty(false);
    }
    
    public PlayerData newPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        String displayName = player.getDisplayName();
        int value = calculateTotal(player);
        
        PlayerData data = new PlayerData(uuid, displayName, value, true, true);
        this.statisticTop.add(data);
        this.statisticTop.sort(COMPARATOR);
        saveData(data);
        
        return data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PlayerData> getStatisticTop() {
        return statisticTop;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public int getDisplayCount() {
        return displayCount;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    private int calculateTotal(Player player) {
        if (statistic.isBlock()) {
            Material[] materials = Material.values();
            int total = 0;
            for (int i = 0; i < materials.length; i++) {
                Material material = materials[i];
                if (!material.isLegacy() && material.isBlock()) {
                    total += player.getStatistic(statistic, material);
                }
                
            }
            return total;
        } else {
            return player.getStatistic(statistic);
        }
    }
    
    public boolean isRefreshing() {
        return booleanIsRefreshing.get();
    }
    
    public List<PlayerData> getPlayersToDisplay() {
        return playersToDisplay;
    }
    
    public class RefreshRunnable extends BukkitRunnable {

        @Override
        public void run() {
            if (isDirty()) {
                booleanIsRefreshing.set(true);
                setDirty(false);
                statisticTop.sort(COMPARATOR);
                playersToDisplay.clear();
                int count = 0;
                for (int i = 0, size = statisticTop.size(); i < size; i++) {
                    if (count < displayCount) {
                        PlayerData data = statisticTop.get(i);
                        if (!data.isVisible())
                            continue;
                        count++;
                        playersToDisplay.add(data);
                    } else {
                        break;
                    }
                }
                booleanIsRefreshing.set(false);
            }
        }
    }
}
