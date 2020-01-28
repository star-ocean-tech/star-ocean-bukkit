package xianxian.mc.starocean.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class DefaultScreen extends Screen {
    private String title;
    private List<String> lines;
    private List<String> proceedLines = new ArrayList<>();

    public DefaultScreen(ScoreboardScreen module, Player player) {
        super(module, player);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<String> getLines() {
        if (module.hasPlaceholderAPISupport())
            return getLinesProceed();
        else
            return lines;
    }
    
    public List<String> getLinesProceed() {
        return proceedLines;
    }

    public void setTitle(String title) {
        this.title = title;
        this.objective.setDisplayName(title);
        setDirty(true);
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
        setDirty(true);
    }

    public void refresh() {
        if (module.hasPlaceholderAPISupport()) {
            proceedLines.clear();
            lines.forEach((s) -> proceedLines.add(PlaceholderAPI.setPlaceholders(player, s)));
            scoreboard.getEntries().forEach((e) -> {
                if (!lines.contains(e)) {
                    setDirty(true);
                }
            });
        }
        super.refresh();
    }
}
