package xianxian.mc.starocean.scoreboard;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class Screen {
    protected final Player player;
    protected final ScoreboardScreen module;
    private boolean dirty = true;
    protected Scoreboard scoreboard;
    protected Objective objective;
    
    public Screen(ScoreboardScreen module, Player player) {
        this.module = module;
        this.player = player;
        scoreboard = module.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective(module.getModuleName().toLowerCase(), "dummy", "");
    }
    
    public abstract String getTitle();
    public abstract List<String> getLines();
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Player getPlayer() {
        return player;
    }

    public void show() {
        setDirty(true);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        refresh();
    }

    public void hide() {
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        scoreboard.getEntries().forEach((s)->scoreboard.resetScores(s));
    }

    public void refresh() {
        if (isDirty()) {
            List<String> lines = getLines();
            scoreboard.getEntries().forEach((e) -> {
                if (!lines.contains(e)) {
                    scoreboard.resetScores(e);
                }
            });
            for (int i = 0, size = lines.size(); i < size; i++) {
                String s = lines.get(i);
                objective.getScore(s).setScore(size - i);
            }
            player.setScoreboard(scoreboard);
            setDirty(false);
        }   
    }
}
