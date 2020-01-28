package xianxian.mc.starocean.dailyrewards;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class Reward {
    private int day;
    private List<String> rewardInfo;
    private List<String> executes;

    public Reward(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public List<String> getRewardInfo() {
        return rewardInfo;
    }

    public List<String> getExecutes() {
        return executes;
    }

    public void read(ConfigurationSection section) {
        day = section.getInt("day");
        rewardInfo = section.getStringList("reward-info");
        executes = section.getStringList("executes");
    }
}
