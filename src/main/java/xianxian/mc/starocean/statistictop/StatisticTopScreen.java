package xianxian.mc.starocean.statistictop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import xianxian.mc.starocean.scoreboard.ScoreboardScreen;
import xianxian.mc.starocean.scoreboard.Screen;

public class StatisticTopScreen extends Screen {
    private StatisticTop module;
    private List<PlayerData> playersToDisplay = new ArrayList<PlayerData>();

    public StatisticTopScreen(StatisticTop myModule, ScoreboardScreen module, Player player) {
        super(module, player);
        this.module = myModule;
        this.objective.setDisplayName(getTitle());
    }

    @Override
    public String getTitle() {
        return module.getTitle();
    }

    @Override
    public List<String> getLines() {
        return null;
    }

    @Override
    public boolean isDirty() {
        return module.isDirty();
    }

    @Override
    public void refresh() {
        module.getPlugin().newTaskChain().async(() -> {
            List<PlayerData> datas = module.getStatisticTop();
            for (int i = 0, count = module.getDisplayCount(), size = datas.size(); i <= count && i < size; i++) {
                PlayerData data = datas.get(i);
                if (data.isVisible()) {
                    if (!playersToDisplay.contains(data))
                        playersToDisplay.add(data);
                    Score score = objective.getScore(data.getDisplayName());
                    if (score.getScore() != data.getValue()) {
                        score.setScore(data.getValue());
                    }
                } else {
                    scoreboard.resetScores(data.getDisplayName());
                    count++;
                }
            }

            playersToDisplay.sort(StatisticTop.COMPARATOR);
            playersToDisplay.removeIf((data) -> {
                if (playersToDisplay.indexOf(data) >= module.getDisplayCount()) {
                    scoreboard.resetScores(data.getDisplayName());
                    return true;
                } else
                    return false;
            });
        });

        player.setScoreboard(scoreboard);
    }
}
