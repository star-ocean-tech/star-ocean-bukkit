package xianxian.mc.starocean.war;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import xianxian.mc.starocean.scoreboard.ScoreboardScreen;
import xianxian.mc.starocean.war.profession.Profession;
import xianxian.mc.starocean.war.skill.Skill;

public class PlayerContext {
    private final StarOceanWar module;
    private final War war;
    private final Player player;
    private final WarScreen screen;
    private ScoreboardScreen screenModule;
    private Location spawnLocation;
    private Location prevLocation;
    private Profession profession;
    private boolean ready;
    
    private Map<Skill, Integer> cooldown;
    
    public PlayerContext(StarOceanWar module, War war, Player player) {
        this.player = player;
        this.module = module;
        this.war = war;
        this.screenModule = module.getPlugin().getModuleManager().getLoadedModule(ScoreboardScreen.class);
        this.screen = new WarScreen(screenModule, war, player);
        this.screen.setTitle(this.module.getTitle());
        this.screen.setLines(this.module.getScreenLines());
        this.spawnLocation = player.getBedSpawnLocation();
        this.prevLocation = player.getLocation();
    }
    
    public void join() {
        this.screenModule.setScreen(player, screen);
        this.screen.show();
        this.player.teleport(war.getBase());
        this.player.setBedSpawnLocation(war.getBase(), true);
        //this.player.
    }
    
    public void quit() {
        this.screenModule.resetToDefaultScreen(player);
        this.player.getInventory().clear();
        this.player.teleport(prevLocation);
        this.player.setBedSpawnLocation(spawnLocation, true);
    }

    public War getWar() {
        return war;
    }

    public Player getPlayer() {
        return player;
    }

    public WarScreen getScreen() {
        return screen;
    }

    public Location getPrevLocation() {
        return prevLocation;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
}
