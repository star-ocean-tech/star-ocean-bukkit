package xianxian.mc.starocean.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import xianxian.mc.starocean.war.tasks.AbstractPendingTask;

public class WarWaves {
    private int wave;
    private final Map<MythicMob, Integer> attackersCount = new HashMap<>();
    
    public WarWaves(int wave) {
        this.wave = wave;
    }
    
    public int getWave() {
        return wave;
    }
    
    private List<AbstractPendingTask> tasks = new ArrayList<>();
    
    public List<AbstractPendingTask> getTasks() {
        return tasks;
    }
    
    public void addTask(AbstractPendingTask task) {
        this.tasks.add(task);
    }
    
    public void addAttackerCount(MythicMob mob, int count) {
        Integer old = this.attackersCount.get(mob);
        this.attackersCount.put(mob, old == null ? count : old + count);
    }

    public Map<MythicMob, Integer> getAttackersCount() {
        return attackersCount;
    }
    
    
}
