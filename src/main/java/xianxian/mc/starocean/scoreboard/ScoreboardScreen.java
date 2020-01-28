package xianxian.mc.starocean.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.placeholderapi.PlaceHolderAPIFeatures;

public class ScoreboardScreen extends Module implements Listener {
    private Map<Player, DefaultScreen> defaultScreens = new HashMap<>();
    private Map<Player, Screen> playerScreens = new HashMap<>();

    private String deafultTitle;
    private List<String> defaultLines = new ArrayList<>();
    
    private BukkitRunnable refreshRunnable;
    private double refreshInterval;
    private boolean placeholderAPISupport;

    public ScoreboardScreen(AbstractPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DefaultScreen screen = new DefaultScreen(this, event.getPlayer());
        screen.setTitle(deafultTitle);
        screen.setLines(defaultLines);
        if (defaultLines.size() != 0)
            screen.show();
        defaultScreens.put(event.getPlayer(), screen);
        playerScreens.put(event.getPlayer(), screen);
        
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        defaultScreens.remove(event.getPlayer());
        Screen scoreboard = playerScreens.remove(event.getPlayer());
        if (scoreboard != null)
            scoreboard.hide();
    }
    
    public String getDeafultTitle() {
        return deafultTitle;
    }

    public List<String> getDefaultLines() {
        return defaultLines;
    }
    
    public void setScreen(Player player, Screen screen) {
        Screen scoreboard = playerScreens.remove(player);
        if (scoreboard != null)
            scoreboard.hide();
        playerScreens.put(player, screen);
    }
    
    public void resetToDefaultScreen(Player player) {
        Screen scoreboard = playerScreens.remove(player);
        if (scoreboard != null)
            scoreboard.hide();
        DefaultScreen screen = this.defaultScreens.get(player);
        if (screen != null) {
            playerScreens.put(player, screen);
            if (defaultLines.size() != 0)
                screen.show();
        }
    }
    
    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        reload();
        try {
            placeholderAPISupport = this.getPlugin().getModuleManager()
                    .<PlaceHolderAPIFeatures>isModuleLoaded(PlaceHolderAPIFeatures.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        if (refreshRunnable != null && !refreshRunnable.isCancelled())
            refreshRunnable.cancel();
    }

    @Override
    public void reload() {
        this.reloadConfig();
        FileConfiguration config = getConfig();
        config.addDefault("scoreboard.title", "");
        config.addDefault("scoreboard.lines", Arrays.asList());
        config.addDefault("refresh-interval", 1D);
        saveConfig();
        deafultTitle = ChatColor.translateAlternateColorCodes('&', config.getString("scoreboard.title", ""));
        List<String> rawLines = config.getStringList("scoreboard.lines");
        refreshInterval = config.getDouble("refresh-interval", 1D);
        defaultLines.clear();
        rawLines.forEach((l) -> {
            defaultLines.add(ChatColor.translateAlternateColorCodes('&', l));
        });

        if (refreshRunnable != null && !refreshRunnable.isCancelled()) {
            refreshRunnable.cancel();
        }
        refreshRunnable = new RefreshRunnable();
        refreshRunnable.runTaskTimer(this.getPlugin(), 20, (int) (refreshInterval * 20D));
    }

    public boolean hasPlaceholderAPISupport() {
        return placeholderAPISupport;
    }
    
    private class RefreshRunnable extends BukkitRunnable {
        @Override
        public void run() {
            ScoreboardScreen.this.playerScreens.values().forEach((s) -> s.refresh());
        }
    }
}
