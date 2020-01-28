package xianxian.mc.starocean.war.tasks;

import xianxian.mc.starocean.war.War;

public abstract class AbstractPendingTask {
    public void init(War war) {}
    public abstract void tick(War war);
    public abstract boolean isFinished();
}
