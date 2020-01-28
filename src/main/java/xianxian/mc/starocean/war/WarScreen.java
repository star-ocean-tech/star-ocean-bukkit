package xianxian.mc.starocean.war;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.clip.placeholderapi.PlaceholderAPI;
import xianxian.mc.starocean.scoreboard.ScoreboardScreen;
import xianxian.mc.starocean.scoreboard.Screen;

public class WarScreen extends Screen {
    private String title;
    private List<String> lines = new ArrayList<>();
    private List<String> proceedLines = new ArrayList<>();
    private War war;
    
    private List<MythicMob> mobs = new ArrayList<>();
    
    private static final Logger LOGGER = Logger.getLogger("DEBUG");
    
    public WarScreen(ScoreboardScreen module, War war, Player player) {
        super(module, player);
        this.war = war;
    }
    
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<String> getLines() {
        return getLinesProceed();
    }
    
    public List<String> getLinesProceed() {
        proceedLines.clear();
        lines.forEach((s) -> {
            String proceed = s.replace("${wave}", String.valueOf(this.war.getCurrentWaveNum()))
                    .replace("${wave_total}", String.valueOf(this.war.getWaves().size()));
            if (proceed.contains("${mobs}")) {
                this.mobs.forEach((mob)->{
                    Integer count = this.war.getAttackersCount().get(mob);
                    String entry = mob.getDisplayName().get();
                    if (count == null)
                        count = 0;
                    entry = entry + "x" + count;
                    proceedLines.add(PlaceholderAPI.setPlaceholders(player, proceed.replace("${mobs}", entry)));
                });
            } else {
                proceedLines.add(PlaceholderAPI.setPlaceholders(player, proceed));
            }
        });
        return proceedLines;
    }

    public void setTitle(String title) {
        this.title = title;
        this.objective.setDisplayName(title);
        setDirty(true);
    }

    public void onWaveChange() {
        this.mobs.clear();
        this.mobs.addAll(this.war.getAttackersCount().keySet());
        this.war.getAttackersCount().entrySet().forEach((e)->{
            LOGGER.info(e.getKey()+"x"+e.getValue());
        });
        this.setDirty(true);
        LOGGER.info("wave changed");
    }
    
    public void setLines(List<String> lines) {
        this.lines = lines;
        setDirty(true);
    }
}
