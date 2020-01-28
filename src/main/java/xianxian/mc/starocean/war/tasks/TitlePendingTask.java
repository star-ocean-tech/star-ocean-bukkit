package xianxian.mc.starocean.war.tasks;

import java.util.List;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.war.War;

public class TitlePendingTask extends AbstractPendingTask {
    private final int seconds;
    private final String title;
    private final String timeout;
    private String subTitle;
    private boolean showSubTitle;
    private final int nextWave;
    
    private int secondsLeft;
    private int ticksBeforeNextShow;
    
    private TitlePendingTask(int seconds, String title, String timeout, int nextWave) {
        this.seconds = seconds;
        this.title = title;
        this.timeout = timeout;
        this.nextWave = nextWave;
    }
    
    private void setSubtitle(String subtitle) {
        this.subTitle = subtitle;
        this.showSubTitle = true;
    }
    
    @Override
    public void init(War war) {
        ticksBeforeNextShow = 20;
        secondsLeft = seconds;
    }

    @Override
    public void tick(War war) {
        ticksBeforeNextShow--;
        
        if (ticksBeforeNextShow <= 0) {
            List<Player> players = war.getPlayers();
            secondsLeft--;
            if (secondsLeft <= 0) {
                String title = this.timeout.replace("${wave}", String.valueOf(nextWave));
                players.forEach((player)->{
                    player.sendTitle(ChatColor.RED.toString(), title, 0, 40, 20);
                });
                
            } else {
                String title = this.title.replace("${seconds}", String.valueOf(secondsLeft)).
                        replace("${wave}", String.valueOf(nextWave));
                players.forEach((player)->{
                    player.sendTitle(ChatColor.RED.toString(), title, 0, 40, 20);
                });
            }
            ticksBeforeNextShow = 20;
        }
    }

    @Override
    public boolean isFinished() {
        return secondsLeft <= 0;
    }
    
    public static class Builder {
        private int seconds;
        private String title;
        private String timeout; 
        private int nextWave;
        
        public Builder seconds(int seconds) {
            this.seconds = seconds;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder timeout(String timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder nextWave(int wave) {
            this.nextWave = wave;
            return this;
        }
        
        public TitlePendingTask build() {
            TitlePendingTask task = new TitlePendingTask(seconds, title, timeout, nextWave);
            return task;
        }
    }
}
