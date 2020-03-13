package xianxian.mc.starocean.minigames.tnttag;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import xianxian.mc.starocean.minigames.tnttag.TNTTag.TNTTagScoreboard;

public class PlayerContext {
    private TNTTag module;
    private Player player;
    private TNTTagScoreboard screen;
    private Game currentGame;
    private PlayerState state = PlayerState.NONE;
    private int passCooldown = 60;

    public PlayerContext(TNTTag module, Player player) {
        this.module = module;
        this.player = player;
        this.screen = new TNTTagScoreboard(module.getScreenModule(), this);
    }

    public static PlayerContext fromPlayer(Player player) {
        if (!player.hasMetadata("TNTTag-PlayerContext"))
            return null;
        return (PlayerContext) player.getMetadata("TNTTag-PlayerContext").get(0).value();
    }
    
    public void join() {
        module.getScreenModule().setScreen(getPlayer(), screen);
        screen.show();
        this.player.setGameMode(GameMode.ADVENTURE);
    }
    
    public void quit() {
        module.getScreenModule().resetToDefaultScreen(getPlayer());
    }
    
    
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public TNTTagScoreboard getScreen() {
        return screen;
    }

    public void setScreen(TNTTagScoreboard screen) {
        this.screen = screen;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }

    public int getPassCooldown() {
        return passCooldown;
    }

    public void setPassCooldown(int passCooldown) {
        this.passCooldown = passCooldown;
    }
    
    public void tick() {
        if (passCooldown > 0)
            passCooldown--;
        
    }

    public enum PlayerState {
        NONE, READY, ALIVE, TAGGED, DEAD
    }
}
