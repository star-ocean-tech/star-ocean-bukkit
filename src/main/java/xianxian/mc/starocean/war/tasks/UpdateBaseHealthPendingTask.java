package xianxian.mc.starocean.war.tasks;

import xianxian.mc.starocean.war.War;

public class UpdateBaseHealthPendingTask extends AbstractPendingTask {
    private int ticksBeforeNextUpdate;
    
    @Override
    public void init(War war) {
        ticksBeforeNextUpdate = 5;
    }
    
    @Override
    public void tick(War war) {
        ticksBeforeNextUpdate--;
        if (ticksBeforeNextUpdate <= 0) {
            war.updateBaseHealth();
            ticksBeforeNextUpdate = 5;
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
