package xianxian.mc.starocean.war.tasks;

import java.util.Objects;

import org.bukkit.Location;

import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import xianxian.mc.starocean.war.Path;
import xianxian.mc.starocean.war.War;
import xianxian.mc.starocean.war.mythicmobs.Attacker;

public class SpawnPendingTask extends AbstractPendingTask {
    private final MythicMob mob;
    private final int level;
    private final int count;
    private final int intervalInTicks;
    private final Location spawnAt;
    private final Path path;
    
    private int ticksBeforeNextSpawn;
    private int mobCountLeft;
    
    public SpawnPendingTask(MythicMob mob, int level, int count, int intervalInTicks, Location spawnAt, Path path) {
        this.mob = mob;
        this.level = level;
        this.count = count;
        this.intervalInTicks = intervalInTicks;
        this.spawnAt = spawnAt;
        this.path = path;
    }

    @Override
    public void init(War war) {
        this.ticksBeforeNextSpawn = intervalInTicks;
        this.mobCountLeft = count;
    }
    
    @Override
    public void tick(War war) {
        if (intervalInTicks == 0) {
            for (int i = 0; i < count; i++) {
                Attacker attacker = new Attacker(mob.spawn(BukkitAdapter.adapt(spawnAt), level));
                attacker.setPath(path);
                war.spawnMob(attacker);
            }
            mobCountLeft = 0;
        }
        
        ticksBeforeNextSpawn--;

        if (ticksBeforeNextSpawn <= 0) {
            Attacker attacker = new Attacker(mob.spawn(BukkitAdapter.adapt(spawnAt), level));
            attacker.setPath(path);
            war.spawnMob(attacker);
            mobCountLeft--;
            ticksBeforeNextSpawn = intervalInTicks;
        }
    }
    
    @Override
    public boolean isFinished() {
        return mobCountLeft <= 0;
    }
    
    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static class Builder {
        private MythicMob mob;
        private int level;
        private int count;
        private int intervalInTicks;
        private Location spawnAt;
        private Path path;
        
        public Builder() {
        }
        
        public Builder mob(MythicMob mob) {
            this.mob = Objects.requireNonNull(mob, "Unable to find this kind of mob");
            return this;
        }
        
        public Builder mobLevel(int level) {
            this.level = level;
            return this;
        }
        
        public Builder count(int count) {
            this.count = count;
            return this;
        }
        
        public Builder interval(double second) {
            this.intervalInTicks = (int) (second * 20D);
            return this;
        }
        
        public Builder spawnAt(Location location) {
            this.spawnAt = Objects.requireNonNull(location, "The location is not found");
            return this;
        }
        
        public Builder path(Path path) {
            this.path = path;
            return this;
        }
        
        public SpawnPendingTask build() {
            SpawnPendingTask task = new SpawnPendingTask(mob, level, count, intervalInTicks, spawnAt, path);
            return task;
        }
    }
}