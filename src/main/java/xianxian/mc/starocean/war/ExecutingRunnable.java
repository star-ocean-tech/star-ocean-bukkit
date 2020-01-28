package xianxian.mc.starocean.war;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import xianxian.mc.starocean.war.tasks.AbstractPendingTask;

public class ExecutingRunnable extends BukkitRunnable {
    private List<AbstractPendingTask> tasks = new ArrayList<>();
    private final War war;
    private final boolean sync;
    
    public ExecutingRunnable(War war, boolean sync) {
        this.war = war;
        this.sync = sync;
    }
    
    @Override
    public void run() {
        if (tasks.size() == 0)
            return;
        if (sync) {
            AbstractPendingTask t = tasks.get(0);
            if (t != null) {
                t.tick(war);
                if (t.isFinished()) {
                    tasks.remove(t);
                }
            }
        } else {
            tasks.forEach((task)->task.tick(war));
            tasks.removeIf((task)->task.isFinished());
        }
    }
    
    public void schedule(AbstractPendingTask... tasks) {
        this.tasks.addAll(Arrays.asList(tasks));
    }
    
    public static class TaskGroup extends AbstractPendingTask {
        private List<AbstractPendingTask> finishedTasks = new ArrayList<>();
        private List<AbstractPendingTask> tasks = new ArrayList<>();
        
        @Override
        public void init(War war) {
            this.tasks.addAll(finishedTasks);
            this.finishedTasks.clear();
            tasks.forEach((t)->t.init(war));
        }
        
        @Override
        public void tick(War war) {
            tasks.forEach(task->{
                task.tick(war);
            });

            tasks.removeIf(task->{
                boolean finished = task.isFinished();
                if (finished) {
                    this.finishedTasks.add(task);
                }
                return finished;
            });
        }

        @Override
        public boolean isFinished() {
            return tasks.size() == 0;
        }
        
        public TaskGroup with(List<AbstractPendingTask> tasks) {
            this.tasks.addAll(tasks);
            return this;
        }
    }
}