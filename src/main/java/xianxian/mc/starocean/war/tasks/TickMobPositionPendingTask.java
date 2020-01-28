package xianxian.mc.starocean.war.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xianxian.mc.starocean.war.War;
import xianxian.mc.starocean.war.mythicmobs.Attacker;

public class TickMobPositionPendingTask extends AbstractPendingTask {
    private int ticksBeforeNextCheck;
    
    private List<Attacker> attackersToRemove = new ArrayList<>();
    
    public TickMobPositionPendingTask() {
        
    }
    
    @Override
    public void init(War war) {
        super.init(war);
        ticksBeforeNextCheck = 20;
    }

    @Override
    public void tick(War war) {
        ticksBeforeNextCheck--;
        
        if (ticksBeforeNextCheck <= 0) {
            ticksBeforeNextCheck = 20;
        } else {
            return;
        }
        war.getAttackers().forEach((mob)->{
            if (mob.getLocation().distance2DSquared(war.getBaseLocationAdapted()) < 2) {
                war.baseUnderAttack((int)Math.ceil(mob.getDamage()));
                mob.getEntity().remove();
                attackersToRemove.add(mob);
            }
            if (mob.getEntity().isLiving() && mob.getEntity().getTarget() == null)
                mob.pathTick();
        });
        attackersToRemove.forEach((mob)->{
            war.mobDead(mob.getHandle());
        });
        
        attackersToRemove.clear();
        
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}
