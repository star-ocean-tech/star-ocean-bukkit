package xianxian.mc.starocean.countentities;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class CountEntities extends Module {

    public CountEntities(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        //CommandCountEntities countentities = new CommandCountEntities(this);
        //countentities.registerDefaultPermission();
        //getPlugin().getCommandManager().registerCommand(countentities);
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {
    }

}
