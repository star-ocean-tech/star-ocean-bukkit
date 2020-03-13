package xianxian.mc.starocean.minigames.tnttag;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.maplevels.MapBase;
import xianxian.mc.starocean.maplevels.MapLevels;
import xianxian.mc.starocean.minigames.tnttag.sign.SignManager;
import xianxian.mc.starocean.scoreboard.ScoreboardScreen;
import xianxian.mc.starocean.scoreboard.Screen;

public class TNTTag extends Module {
    private static final String SCOREBOARD_TITLE = "§6|§4星海-丢锅大战§6|";
    private static final List<String> SCOREBOARD_LINES = Collections.unmodifiableList(Arrays.asList(
            "§f§2", 
            "§6§m                           §6§r", 
            "§1第${round}回合",
            "§7场上的玩家${alive_players}/${total_players}",
            "§f§3",
            "§6已开始时间: ${total_time}",
            "§6离爆炸时间: ${explode_time}",
            "§6§m                           §6",
            "§f"));
    private Map<String, Game> games = new HashMap<>();
    private GameListener gameListener = new GameListener(this);
    private SignManager signManager = new SignManager(this, new File(getDataFolder(true), "signs.yml"));
    private ScoreboardScreen screenModule;
    private MapLevels mapModule;
    
    public TNTTag(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.screenModule = getPlugin().getModuleManager().getLoadedModule(ScoreboardScreen.class);
        this.mapModule = getPlugin().getModuleManager().getLoadedModule(MapLevels.class);
        
        this.getPlugin().getServer().getPluginManager().registerEvents(gameListener, getPlugin());
        this.getPlugin().getServer().getPluginManager().registerEvents(signManager, getPlugin());
        
        reload();
        
        TickRunnable runnable = new TickRunnable();
        runnable.runTaskTimer(getPlugin(), 20, 20);
    }

    @Override
    public void disable() {
        this.games.values().forEach((g)->g.stop());
    }

    @Override
    public void reload() {
        reloadConfig();
        
        this.signManager.load();
        
        FileConfiguration config = getConfig();
        
        if (config.isConfigurationSection("games")) {
            ConfigurationSection gamesSection = config.getConfigurationSection("games");
            gamesSection.getKeys(false).forEach((s)->{
                if (!gamesSection.isConfigurationSection(s))
                    return;
                ConfigurationSection gameSection = gamesSection.getConfigurationSection(s);
                
                Game game = new Game(this, s, /*unused*/null, gameSection.getInt("min-players"), gameSection.getInt("max-players"), gameSection.getLocation("lobby-location"), gameSection.getLocation("start-location"));
                
                this.games.put(s, game);
            });
        }
    }
    
    public Game getGame(String name) {
        return games.get(name);
    }
    
    public MapLevels getMapModule() {
        return mapModule;
    }
    
    public SignManager getSignManager() {
        return signManager;
    }
    
    private class TickRunnable extends BukkitRunnable {
        @Override
        public void run() {
            games.values().forEach((game)->{
                if (game.isTicking())
                    game.tick();
            });
        }
    }
    
    public ScoreboardScreen getScreenModule() {
        return screenModule;
    }

    public static class TNTTagScoreboard extends Screen {
        private PlayerContext context;

        public TNTTagScoreboard(ScoreboardScreen module, PlayerContext player) {
            super(module, player.getPlayer());
            this.context = player;
            this.objective.setDisplayName(getTitle());
        }

        @Override
        public String getTitle() {
            return SCOREBOARD_TITLE;
        }
        
        @Override
        public boolean isDirty() {
            return true;
        }

        @Override
        public List<String> getLines() {
            if (context.getCurrentGame() == null) 
                return SCOREBOARD_LINES;
            
            List<String> lines = new ArrayList<String>();
            SCOREBOARD_LINES.forEach((line)->{
                lines.add(line.replace("${round}", String.valueOf(context.getCurrentGame().getRound()))
                        .replace("${alive_players}", String.valueOf(context.getCurrentGame().getAlivePlayers().size()))
                        .replace("${total_players}", String.valueOf(context.getCurrentGame().getPlayers().size()))
                        .replace("${total_time}", Game.secondToString(context.getCurrentGame().getTotalTime()))
                        .replace("${explode_time}", Game.secondToString(context.getCurrentGame().getExplodeCountdown())));
            });
            return lines;
        }

    }
}
